package com.gzqylc.da.service;

import com.gzqylc.da.entity.Registry;
import com.gzqylc.da.entity.Repository;
import com.gzqylc.da.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IRepositoryService {
    Page<Repository> findRepositoryList(Pageable pageable, Registry registry, String keyword) throws Exception;

    List<Tag> findTagList(String url, Registry registry) throws Exception;
}
