package com.gzqylc.docker.admin.service;

import com.aliyuncs.exceptions.ClientException;
import com.gzqylc.docker.admin.entity.Registry;
import com.gzqylc.docker.admin.entity.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRepositoryService {
    Page<Repository> findRepositoryList(Pageable pageable, Registry registry, String keyword) throws Exception;

    List<String> findTagList(String url, Registry registry) throws Exception;

    void deleteTag(String imageUrl, Registry registry) throws ClientException;
}
