# 深入浅出RxJava4：在Android中的应用
在第1,2,3篇中，我介绍了RxJava是如何工作的。但是作为一个Android 开发人员，怎样才能让RxJava能为你所用呢。以下是一些Android开发者的实用信息。

#### RxAndroid 
RxAndroid 是RxJava针对Android平台的扩展。它包括一些特比的绑定，可以让你的开发工作更轻松。  
首先，AndroidSchedulers提供了针对Android的线程系统的调度器。需要在UI线程中运行某些代码？很简单，只需要使用AndroidSchedulers.mainThread():
```
retrofitService.getImage(url)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(bitmap -> myImageView.setImageBitmap(bitmap));
```
如果你创建了自己的Handler，你可以使用HandlerThreadScheduler将一个调度器链接到你的handler上。  

下一步，就是AndroidObservable，它提供了跟多的功能来配合Android的生命周期。这里的bindActivity()和bindFragment()方法默认使用AndroidSchedulers.mainThread()来执行观察者代码，而且这两个方法会在Activity或者Fragment结束的时候通知被观察者停止发出新的消息。
```
AndroidObservable.bindActivity(this, retrofitService.getImage(url))
    .subscribeOn(Schedulers.io())
    .subscribe(bitmap -> myImageView.setImageBitmap(bitmap));
```
我同样喜欢AndroidObservable.fromBroadcast()方法，它允许你创建一个类似BroadcastReceiver的Observable对象。下面的例子展示了如何在网络变化的时候被通知到：
```
IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
AndroidObservable.fromBroadcast(context, filter)
    .subscribe(intent -> handleConnectivityChange(intent));
```
最后就是ViewObservable，使用它可以给View添加了一些绑定。如果你想在每次点击view的时候都收到一个事件，可以使用ViewObservable.clicks()，或者你想监听TextView的内容变化，可以使用ViewObservable.text()。
```
ViewObservable.clicks(mCardNameEditText, false)
    .subscribe(view -> handleClick(view));
```
#### Retrofit
有一个著名的Android库支持RxJava：Retrofit。  
一般的情况下，当你需要定义一个异步方法时，你需要添加回调方法：
```
@GET("/user/{id}/photo")
void getUserPhoto(@Path("id") int id, Callback<Photo> cb);
``` 
使用RxJava的话，你可以直接返回一个Observable对象：  

```
@GET("/user/{id}/photo")
Observable<Photo> getUserPhoto(@Path("id") int id);
```
现在你可以随意使用Observable对象了。你不仅可以获取数据，还可以进行变换。  

Retrofit对Observable的支持使得它可以很简单的将多个REST请求结合起来。比如我们有一个请求是获取照片的，还有一个请求是获取元数据的，我们就可以将这两个请求并发的发出，并且等待两个结果都返回之后再做处理：
```
Observable.zip(
    service.getUserPhoto(id),
    service.getPhotoMetadata(id),
    (photo, metadata) -> createPhotoWithData(photo, metadata))
    .subscribe(photoWithData -> showPhoto(photoWithData));
```
在第二篇里我曾展示过一个类似的例子（使用flatMap()）。这里我只是想展示以下使用RxJava+Retrofit可以多么简单地组合多个REST请求。

#### 老的遗留的代码
Retrofit可以返回Observable对象，但是如果你使用的别的库并不支持这样怎么办？或者说一个内部的内码，你想把他们转换成Observable的？有什么简单的办法没？主要是，如何在不重写所有内容的情况下将旧代码连接到新代码。  

绝大多数时候Observable.just() 和 Observable.from() 能够帮助你从遗留代码中创建 Observable 对象: 
```
private Object oldMethod() { ... }

public Observable<Object> newMethod() {
    return Observable.just(oldMethod());
}
```
上面的例子中如果oldMethod()足够快是没有什么问题的，但是如果很慢呢？在使用Observable.just之前，调用oldMethod()将会阻塞住他所在的线程。  
为了解决这个问题，可以参考我一直使用的方法–使用defer()来包装缓慢的代码：
```
private Object slowBlockingMethod() { ... }

public Observable<Object> newMethod() {
    return Observable.defer(() -> Observable.just(slowBlockingMethod()));
}
``` 
现在，newMethod()的调用不会阻塞了，除非你订阅返回的observable对象。  
#### 生命周期
我把最难的部分留在最后。


[参考原文](https://blog.danlew.net/2014/10/08/grokking-rxjava-part-4/)