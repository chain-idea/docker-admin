package com.gzqylc.da.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.gzqylc.da.entity.Registry;
import com.gzqylc.da.entity.Repository;
import com.gzqylc.da.entity.Tag;
import com.gzqylc.lang.web.JsonTool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// registry.cn-hangzhou.aliyuncs.com
public class RepositoryServiceAliyunImpl implements IRepositoryService {


    public Page<Repository> findRepositoryList(Pageable pageable, Registry registry, String keyword) throws Exception {
        IAcsClient client = getClient(registry);
        CommonRequest request = getCommonRequest(registry);

        request.setUriPattern("/repos");
        String requestBody = "" +
                "{}";
        request.setHttpContent(requestBody.getBytes(), "utf-8", FormatType.JSON);
        request.putQueryParameter("PageSize", String.valueOf(pageable.getPageSize()));
        request.putQueryParameter("Page", String.valueOf(pageable.getPageNumber() + 1));
        if (keyword != null) {
            request.putQueryParameter("RepoNamePrefix", keyword);
        }
        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();


        return convertRepository(data, pageable);
    }

    @Override
    public List<Tag> findTagList(String url, Registry registry) throws Exception {
        IAcsClient client = getClient(registry);
        CommonRequest request = getCommonRequest(registry);

        String subUrl = url.substring(url.indexOf("/") + 1);


        request.setUriPattern("/repos/" + subUrl + "/tags");
        String requestBody = "" +
                "{}";
        request.setHttpContent(requestBody.getBytes(), "utf-8", FormatType.JSON);
        request.putQueryParameter("PageSize", String.valueOf(99));
        request.putQueryParameter("Page", String.valueOf(1));

        CommonResponse response = client.getCommonResponse(request);
        String data = response.getData();

        Map<String, Object> map = JsonTool.jsonToMap(data);
        List<Map> tags = (List<Map>) ((Map)map.get("data")).get("tags");


        return tags.stream().map(t -> {
            Tag tag = new Tag();
            tag.setTag((String) t.get("tag"));
            Long imageSize = Long.valueOf(t.get("imageSize").toString());
            tag.setSize(imageSize);
            tag.setCreateTime(new Date((Long) t.get("imageCreate")));
            tag.setModifyTime(new Date((Long) t.get("imageUpdate")));

            return tag;
        }).collect(Collectors.toList());


    }


    private CommonRequest getCommonRequest(Registry registry) {
        String host = registry.getHost();
        String[] split = host.split("\\.");
        String regionId = split[1];

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.GET);
        request.setDomain("cr." + regionId + ".aliyuncs.com");
        request.setVersion("2016-06-07");
        request.putHeadParameter("Content-Type", "application/json");

        return request;
    }

    private IAcsClient getClient(Registry registry) {
        String host = registry.getHost();
        String[] split = host.split("\\.");
        String regionId = split[1];

        DefaultProfile profile = DefaultProfile.getProfile(
                regionId,
                registry.getAliyunAccessKeyId(),
                registry.getAliyunAccessKeySecret());

        IAcsClient client = new DefaultAcsClient(profile);

        return client;

    }

    private Page<Repository> convertRepository(String json, Pageable pageable) throws IOException {

        Map<String, Object> response = JsonTool.jsonToMap(json);
        Map data = (Map) response.get("data");

        Integer total = (Integer) data.get("total");

        List<Repository> list = new ArrayList<>();
        List<Map> repos = (List<Map>) data.get("repos");
        repos.forEach(aliRepos -> {
            Repository r = new Repository();
            r.setName((String) aliRepos.get("repoName"));
            r.setSummary((String) aliRepos.get("summary"));
            r.setType((String) aliRepos.get("repoType"));
            r.setModifyTime(new Date((Long) aliRepos.get("gmtModified")));

            Map<String, String> repoDomainList = (Map<String, String>) aliRepos.get("repoDomainList");

            String domain = repoDomainList.get("public");
            Object namespace = aliRepos.get("repoNamespace");
            String url = domain + "/" + namespace + "/" + r.getName();

            r.setUrl(url);


            list.add(r);
        });


        Page<Repository> page = new PageImpl<>(list, pageable, total);

        return page;
    }

}
