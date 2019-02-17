# 深入浅出RxJava1：基础篇
RxJava是Android开发者中的热门技术。唯一的不足之处是初次上手比较困难。对于命令行式编程语言的人来说，响应式编程有些难以理解，但是一旦你理解了它，你就会发现的魅力所在的。

这里我将尝试给你介绍RxJava的一些基本特点，这个系列中的四篇文章目的是为了带你入门。我不会去解释其中的所有原理，只是希望引起你对RxJava的兴趣并且了解它如何使用。

#### 基础
响应式代码基本组成是 Observables(被观察者，事件源) 和 Subscribers(观察者，订阅者)。Observable 发送了一系列事件，由Subscriber 接收消费这些事件。

这是一系列事件如何被发送的模式。Observable 可能发送了许多事件(包括0个事件)，然后，它要么成功完成，要么由于错误而终止。 对于每个Subscriber都有，Observable每发出一个事件，就会调用它的Subscriber的onNext方法，最后调用Subscriber.onNext()或者Subscriber.onError()结束。

如此这样看起来很像设计模式中的观察者模式，但是有一点明显不同，那就是如果一个Observable没有订阅 Subscriber，那么这个Observable 是不会发送任何事件的。

#### Hello, World!
通过一个实际的例子来看这个框架的运行。首先，创建一个基本Observable：
```
Observable<String> myObservable = Observable.create(
    new Observable.OnSubscribe<String>() {
        @Override
        public void call(Subscriber<? super String> sub) {
            sub.onNext("Hello, world!");
            sub.onCompleted();
        }
    }
);
```
Observable 发送了"Hello, world!" 然后结束了。现在我们创建一个Subscriber来接收这个数据：
```
Subscriber<String> mySubscriber = new Subscriber<String>() {
    @Override
    public void onNext(String s) {
        System.out.println(s);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
    }
};
```
这样仅仅就打印了Observable 发送的字符串。现在通过subscribe()方法就可以将我们定义的myObservable对象和mySubscriber对象关联起来。
```
myObservable.subscribe(mySubscriber);
```
当订阅成功后，myObservable对象就会调用mySubscriber的 onNext() 和 onComplete()方法。结果就是mySubscriber 打印出"Hello, world!"，事件结束。

#### Simpler Code
上面的代码如果仅仅是用来打印"Hello, world!"，那只会让你觉得有些得不偿失。这个例子只是用来让你能够直观的感受RxJava背后的调用原理，实际上RxJava内部提供了许多快捷方式使得编码更加简洁。

首先，我们来简化我们Observable。在RxJava中提供了多个Observable的创建方法。比如，在这个例子中，可以使用Observable.just()来发送一个单个事件，然后完成结束。
```
Observable<String> myObservable = Observable.just("Hello,world!");
```
下一步，我们来处理冗余的Subscriber，这里我们不需要关心onCompleted() 和 onError()，所以我们只需要在onNext的时候做一些处理，这时候就可以使用Action1类。

```
Action1<String> onNextAction = new Action1<String>() {
    @Override
    public void call(String s) {
        System.out.println(s);
    }
};
```
Action 可以被用来定义Subscriber中执行方法onNext，onCompleted，onError的每个部分。比如，用于Observable.subscribe()中的接受三个Action1类型的参数，分别对应OnNext，OnComplete， OnError函数。
```
myObservable.subscribe(onNextAction, onErrorAction, onCompleteAction);
```
这里我们只需要一个参数就行了：

```
myObservable.subscribe(onNextAction);
```
上面最终完整的使用方法：
```
Observable.just("Hello, world!")
        .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
```

如果考虑使用java8的lambda就可以使代码更简洁：
```
Observable.just("Hello, world!")
        .subscribe(s -> System.out.println(s));
```
#### Transformation



[参考原文](https://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/)