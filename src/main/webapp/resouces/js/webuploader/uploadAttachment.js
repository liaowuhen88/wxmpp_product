/**
 * 基于jquery的图片上传控件
 */
!function ($) {

    "use strict";
    //定义上传事件
    var upImgEvent = {
        fileQueued: 'fileQueued',//文件加载的时候触发
        uploadProgress: 'uploadProgress',//文件上传进度

        //statusChange: 'statuschange',
        uploadSuccess: 'uploadSuccess',//文件上传成功触发默认路径应该是固定的
        uploadError: 'uploadError',//文件上传报错触发


        error: 'error',//文件上传验证报错的时候触发,比如大小,上传重复图片，
        uploadFinished: 'uploadFinished',
        startUpload: 'startUpload'
    };

    var attachment = function (element, options) {

        //构造默认options
        var defaultOption = {
            packId: "#" + options.packId,
            swf: window.base + '/resources/js/webuploader/Uploader.swf',//ie6,7,8 需要flash支持，默认优先Html5
            server: window.base + options.uploadServer + options.uploadPath,//文件上传的服务器路径
            pick: {
                id: "#" + options.uploaderId,
                multiple: options.isMultiple//是否单选
            },
            duplicate: true,//可以重复
            sendAsBinary: true,//android4 有些机型必须开启 为了兼容 都启用这种模式
            auto: true,//开启选择图片自动上传
            fileVal: 'bin',//文件上传字段名称
            fileSingleSizeLimit: 10 * 1024 * 1024,  //10M 单个图片只能最大10M
            disableGlobalDnd: true,//不能拖拽
            accept: {
                //上传图片所支持的类型
                title: "上传附件",
                //extensions: 'gif,jpg,jpeg,bmp,png',
                //mimeTypes: 'image/*'
            },
            resize: false,//不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
            callback: undefined || options.callback,
            success: undefined || options.success,
            uploadFinished: undefined || options.uploadFinished,
            startUpload: undefined || options.startUpload
        };
        this.init(defaultOption);
    };
    attachment.prototype.init = function (options) {
        var uploader = WebUploader.create(options);
        //构造监听事件
        uploader.on(upImgEvent.fileQueued, function (file) {
            //$('body').showLoading();
            if (file.getStatus() === 'invalid') {

            }
            if (options.callback != undefined && options.callback instanceof Function) {

                options.callback.apply(this, [file]);

            }
        });
        uploader.on('uploadProgress', function (file, percentage) {
            var $li = $('#' + file.id),
                $percent = $li.find('.progress .progress-bar');

            // 避免重复创建
            if (!$percent.length) {
                $percent = $('<div class="progress progress-striped active">' +
                    '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                    '</div>' +
                    '</div>').appendTo($li).find('.progress-bar');
            }

            $li.find('p.state').text('上传中');

            $percent.css('width', percentage * 100 + '%');
        });
        uploader.on(upImgEvent.uploadSuccess, function (file, response) {
            if (response.success) {
                var imgResponse = response.data;
                if (options.success != undefined && options.success instanceof Function) {
                    options.success.apply(this, [file, imgResponse]);
                }
            } else {
                alert("warning:" + response.msg);
            }
        });
        uploader.on(upImgEvent.uploadFinished, function () {
            if (options.uploadFinished != undefined && options.uploadFinished instanceof Function) {
                options.uploadFinished.apply();
            }
        });
        uploader.on(upImgEvent.uploadError, function (file, code) {
            //$('body').hideLoading();
            $("img").next().remove();
            alert("文件上传失败" + file.name + "code:" + code);
        });
        uploader.on(upImgEvent.startUpload, function (file, code) {
            options.startUpload();
        });

        uploader.on(upImgEvent.error, function (code) {
            //$('body').hideLoading();
            $("img").next().remove();
            if (code === 'F_DUPLICATE')
                alert("文件已经存在.请换个图片重新上传...");
            else if (code === 'F_EXCEED_SIZE')
                alert("文件不能超过10M,请重新上传");
            else if (code === 'Q_TYPE_DENIED')
                alert("文件类型只能是图片类型,请重新上传");
            else
                alert("error type :" + code);
        });
    };
    $.attachmentUploader = function (options) {
        return new attachment(this, options);
    };
    return window.attachment = attachment;
}(window.jQuery);
function deleteImg(id, type) {
    $("#" + id).remove();
    //如果是sybd则显示上传插件
    if (type == 1) {
        $("#sybd").parent().show();
    }

    //快速理赔
    if (type == 666) {
        $("#fastSybd").show();
    }
}
