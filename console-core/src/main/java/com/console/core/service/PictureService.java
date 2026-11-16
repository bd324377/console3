package com.console.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.Picture;
import org.springframework.web.multipart.MultipartFile;

public interface PictureService extends IService<Picture> {
    Picture addPicture(MultipartFile file);
    IPage<Picture> getPicturePage(Page<Picture> page, Picture picture);
    boolean delBatchPictureByIds(Long... pictureIds);
}
