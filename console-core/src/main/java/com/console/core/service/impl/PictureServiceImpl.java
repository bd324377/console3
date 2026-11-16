package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.core.entity.Picture;
import com.console.core.mapper.PictureMapper;
import com.console.core.service.PictureService;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.domain.BusinessException;
import com.console.framework.request.RequestUser;
import com.console.framework.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PictureServiceImpl extends BaseServiceImpl<PictureMapper,Picture> implements PictureService {
    private static final String UPLOAD_PREFIX = "/uploads/";

    @Value("${console.fileUpload.baseFilePath}")
    private String baseFilePath;

    @Override
    public Picture addPicture(MultipartFile file) {
        String incrementedFileName = addFile(file);
        try {
            RequestUser requestUser = RequestUtils.getUser(RequestUser.class);
            DynamicTableNameHandler.setTenantId(requestUser.getTenantId());
            //将上传的文件记录到数据库
            Picture picture = new Picture();
            picture.setPicName(incrementedFileName);
            picture.setPicPath(UPLOAD_PREFIX + incrementedFileName);
            this.save(picture);
            return picture;
        } finally {
            DynamicTableNameHandler.removeTenantId();
        }
    }

    @Override
    public IPage<Picture> getPicturePage(Page<Picture> page, Picture picture) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .likeRight(StringUtils.hasText(picture.getPicPath()),Picture::getPicPath,picture.getPicPath())
                .likeRight(StringUtils.hasText(picture.getPicName()),Picture::getPicName,picture.getPicName());
        return this.page(page,queryWrapper);
    }

    /**
     * 批量删除已上传图片信息
     * @param pictureIds    待删除图片ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delBatchPictureByIds(Long... pictureIds) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Picture::getId, Arrays.asList(pictureIds));
        List<Picture> pictureList = this.list(queryWrapper);
        for (Picture picture : pictureList) {
            File file = new File(baseFilePath + picture.getPicName());
            if (file.exists()) {
                boolean delResult = file.delete();
                System.out.println("文件删除" + (delResult ? "成功" : "失败"));
            }
        }
        return this.removeBatchByIds(Arrays.asList(pictureIds));
    }

    private String addFile(MultipartFile file) {
        if (file.isEmpty() || !StringUtils.hasText(file.getOriginalFilename())) {
            throw new BusinessException(0, "Please select a file to upload！");
        }
        // 设置文件保存路径
        Path uploadPath = Paths.get(baseFilePath);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // 将文件保存到指定路径
        Path targetFile = uploadPath.resolve(fileName);
        //同名文件处理
        int count = 1;
        String incrementedFileName = fileName;
        String[] fileNames = fileName.split("\\.");
        while (Files.exists(targetFile)) {
            incrementedFileName = fileNames[0] + "_" + count + "." + fileNames[1];
            targetFile = uploadPath.resolve(incrementedFileName);
            count++;
        }
        try {
            file.transferTo(targetFile.toFile());
            return incrementedFileName;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
