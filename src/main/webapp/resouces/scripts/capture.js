/**
 * 历史记录
 * @constructor
 */
'use strict';

var capture = function () {
    /**
     * 截图
     */
    jQuery.capture = function (callback) {
        if (!this.capture.nco) {
            this.capture.nco = new NiuniuCaptureObject();
            this.capture.nco.NiuniuAuthKey = 'niuniu';
            this.capture.nco.TrackColor = rgb2value(17, 119, 17);
            this.capture.nco.EditBorderColor = rgb2value(17, 119, 17);
            this.capture.nco.VersionCallback = function (e) {
            };
            this.capture.nco.PluginLoadedCallback = function () {
            };
            this.capture.nco.FinishedCallback = function (type, x, y, width, height, info, content) {
                var base64 = 'data:image/jpeg;base64,' + content;
                var bytes = atob(content);
                var ab = new ArrayBuffer(bytes.length);
                var ua = new Uint8Array(ab);
                for (var i = 0, l = bytes.length; i < l; i++) {
                    ua[i] = bytes.charCodeAt(i);
                }
                callback && callback(new Blob([ab], {type: 'image/jpeg'}), base64);
            };
            this.capture.nco.InitNiuniuCapture();
            this.capture.nco.onerror = function (e) {
                alert('未检测到截图插件，请安装截图插件');
                var a = document.createElement('a');
                a.href = 'http://www.ggniu.cn/download/CaptureInstall.exe';
                a.click();
            }
        }
        this.capture.nco.DoCapture('1.jpg', 0, 0, 0, 0, 0, 0);
    }


    /**
     * 上传
     */
    function upload(url, data, success) {
        alert('upload');
        var request = new XMLHttpRequest();
        request.onload = function () {
            success && success(request.response);
        }
        request.responseType = 'json';
        request.open('POST', url);
        request.send(Object.entries(data || {}).reduce(function (p, o) {
            return p.append(o[0], o[1]), p;
        }, new FormData()));
    }

    function capture() {
        alert('capture');
        $.capture(function (data, base64) {
            /*设置预览*/
            xxx.src = base64;
            /*上传服务器*/
            upload('http://localhost:3000/upload', {file: data}, function (o) {
                console.log(o);
            });
        });
    }

};
