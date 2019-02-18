# 深入浅出RxJava1：基础篇
RxJava是Android开发者中的热门技术。唯一的不足之处是初次上手比较困难。对于命令行式编程语言的人来说，响应式编程有些难以理解，但是一旦你理解了它，你就会发现的魅力所在的。

这里我将尝试给你介绍RxJava的一些基本特点，这个系列中的四篇文章目的是为了带你入门。我不会去解释其中的所有原理，只是希望引起你对RxJava的兴趣并且了解它如何使用。

#### 基础
响应式代码基本组成是 Observables(被观察者，事件源) 和 Subscribers(观察者，订阅者)。Observable 发送了一系列事件，由Subscriber 接收消费这些事件。

这是一系列事件如何被发送的模式。Observable 可能发送了许多事件(包括0个事件)，然后，它要么成功完成，要么由于错误而终止。 对于每个Subscriber，Observable每发出一个事件，就会调用Subscriber的onNext方法，最后调用Subscriber.onNext()或者Subscriber.onError()结束。

如此这样看起来很像设计模式中的观察者模式，但是有一点明显的不同，如果一个Observable没有订阅 Subscriber，那么这个Observable 是不会发送任何事件的。

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
Observable 发送了"Hello, world!" ，然后告诉事件结束。现在我们创建一个Subscriber来接收这个数据：
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
这样就可以打印出Observable 发送的字符串。现在通过subscribe()方法就可以将我们定义的myObservable对象和mySubscriber对象关联起来。
```
myObservable.subscribe(mySubscriber);
```
当订阅成功后，myObservable对象就会调用mySubscriber的 onNext() 和 onComplete()方法。结果就是mySubscriber 打印出"Hello, world!"，事件结束。

#### Simpler Code
上面的代码如果仅仅是用来打印"Hello, world!"，那只会让你觉得有些大材小用。这个例子只是用来让你能够直观的感受RxJava背后的调用原理，实际上RxJava内部提供了许多快捷方式使得编码更加简洁。

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
在这里我们只需要一个参数就行了：

```
myObservable.subscribe(onNextAction);
```
上面代码完整的使用方法：
```
Observable.just("Hello, world!")
        .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println(s);
            }
        });
```

如果考虑使用java8的lambda特性就可以使代码更简洁：
```
Observable.just("Hello, world!")
        .subscribe(s -> System.out.println(s));
```
#### Transformation
让我们再加上一些有趣的设定吧。
假如我想要在"Hello, world!"后面添加上我的签名，一个可能的想法就是去改变这个Observable：

```
Observable.just("Hello, world! -Dan")
        .subscribe(s -> System.out.println(s));
```
如果你能改变自己的Observable对象，这当然是有效的。但是如果这是别人提供的第三方库，你就不再具有这样的权限了。
这就出现了一个潜在的问题，如果Observable在多处被引用，但是只需要在某一处需要被修改，这该如何呢？

这时如果考虑来修改我们自己的Subscriber对象怎么样呢：

```
Observable.just("Hello, world!")
        .subscribe(s -> System.out.println(s + "-Dan"));
```
这种方式仍然不让你满意，因为我希望我的Subscriber尽可能的轻量级，因为我可能会在main thread 中运行它。另外，根据响应式函数编程的理念，Subscribers更应该做的事情是“响应”，仅去响应Observable发出的事件，而不是去修改事件。

如果我能在某些中间步骤中对“Hello World！”进行转换岂不是很酷？

####Introducing Operators(操作符)
操作符就是为了解决对Observable对象的变换的问题。操作符被用于Observable 数据源和最终的Subscriber之间，来修改Observable发出的事件。RxJava提供了许多有用的操作符集合，但是目前我们只聚焦在少数有用的。

在这里，使用map操作符，就是用来把一个被发送的事件转换为另一个事件：

```
Observable.just("Hello,world!").map(new Func1<String, String>() {
    @Override
    public String call(String s) {
        return s + " - Dan";
    }
}).subscribe(s -> System.out.println(s));
```

同样的，我们可以使用lambdas 简化上面代码：

```
Observable.just("Hello,world!")
        .map(s -> s + " - Dan")
        .subscribe(s -> System.out.println(s));
```
这样是不是很酷！

map() 操作符就是用来变换Observable对象的，map操作符返回一个Observable对象。我们可以通过链式调用map()，在一个Observable对象上多次使用map操作符，最终将我们需要的数据传递给Subscriber对象。

#### More on map() (map 进阶)
map操作符有趣的一点是它不必返回原Observable对象返回的数据类型，即你可以使用map操作符返回一个发出新的数据类型的Observable对象。

比如，在上面的例子中，对Subscriber输出的字符串文本不感兴趣，而是想要输出的是字符串的hash值：

```
Observable.just("Hello, world!")
        .map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return s.hashCode();
            }
        })
        .subscribe(i -> System.out.println(Integer.toString(i)));
```
很有趣吧？我们初始的Observable返回的是字符串，最终的Subscriber收到的却是Integer，当然使用lambda可以进一步简化代码：

```
Observable.just("Hello, world!")
        .map(s -> s.hashCode())
        .subscribe(i -> System.out.println(Integer.toString(i)));
```

如之前所说，我们希望我们的Subscriber做的事情越少越好，我们新增一个map来将hash转化为字符串：

```
Observable.just("Hello, world!")
        .map(s -> s.hashCode())
        .map(i -> Integer.toString(i))
        .subscribe(s -> System.out.println(s));
```
上面代码中已经展示了，我们的Observable 和 Subscriber 还是之前的代码逻辑没有去改变。我们仅仅是在它们之间增加了转换步骤。我们还可以增加签名转化：

```
Observable.just("Hello, world!")
        .map(s -> s + " -Dan")
        .map(s -> s.hashCode())
        .map(i -> Integer.toString(i))
        .subscribe(s -> System.out.println(s));
```
#### So What?

这里你可能在想"对于一些简单的业务，这里的代码逻辑显得非常啰嗦"。的确如此，这里的例子非常简单。但是你需要明白以下两点：

#####1: Observable 和 Subscriber 还能做任何事情

这里的Observable 可以是数据库查询, Subscriber 拿到查询结果然后展示在屏幕中。Observable 也可以是屏幕的点击事件，然后 Subscriber 去响应它。Observable可以是一个网络请求，Subscriber用来显示请求结果。它是一个可以处理任何问题的通用框架。 

#####2: Observable 和 Subscriber 独立于它们之间的转换步骤。
在Observable和Subscriber中间可以增减任何数量的map。整个系统是高度可组合的，操作数据源是一个很简单的过程。

结合这两点，您可以看到一个具有很大潜力的系统。

[参考原文](https://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/)