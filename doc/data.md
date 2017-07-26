## 消息数据

```javascript
{
  content: "消息内容", // 消息内容
  contentType: "text",  // 消息类型   text, video, file, audio, image, receiptMsg
  ct: 1498719294914,  // 消息创建时间
  from: "18638130887@126xmpp",  // 消息来源
  icon: "/kf/resouces/images/user_photo_default.jpg", // 用户头像
  id: "1498719294914-msg",  // 用户ID
  isRead: false,  // 是否已读
  openId: "dctxf",  // 微信公众号用户openID
  src: "18638130887@126xmpp", //
  time: "2017-06-29 14:54:54",  // 又一个时间
  to: "dctxf@126xmpp",  // 发送者
  type: "msg" // 消息类型 active(动作类), msg(消息), status(状态消息例如上下线)
  token: "asdklaslfjllkasjflkawe" // 验证通过后返回的token
  status:""  // 消息状态 
          //只适合客服上线，客服没有队列概念
          customerOnline,
          customerOffline,

          //全适用
          initSuccess,//初始化成功
          initError,//初始化失败
          offline,//下线
          loginSuccess,//登录成功
          loginError,//登录失败
          kickOff,//被服务端踢出，账号重复登录
          serverACK,//服务器消息确认ack
  
          //进入队列成功状态
          onlineQueueSuccess,//上线不等待
          waitQueueSuccess,//上线且等待
          backUpQueueSuccess,//上线到backup队列
  
          offlineWaitQueue,//离开等待队列 准备进入到线上队列
          offlineBackUpQueue,//离开backup队列
  
          //进入队列失败状态
          onlineQueueError,//进入线上队列失败
          waitQueueError,//进入等待队列失败
          backUpQueueError//进入backup队列失败
}

```