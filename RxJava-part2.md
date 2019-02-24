# 深入浅出RxJava2：操作符
在第一篇中，我介绍了RxJava的基本结构，同样也介绍了map操作符。然而，在没有进行更多的学习下，还是能够理解你并没有意愿去使用RxJava。但是，情形很快就会发生改变，RxJava的强大功能很大一部分来自框架中包含的所有操作符。  
让我们通过一个例子向你介绍更多的操作符。

#### 准备工作
假如我有这样一个方法  
这个方法根据输入的字符串返回一个网站的url列表

```
Observable<List<String>> query(String text); 
```
我想构建一个鲁棒性的系统，可以来查询字符串并显示结果。结合上一篇文章内容，我们可能会有下面方法实现：

```
query("Hello, world!")
    .subscribe(urls -> {
        for (String url : urls) {
            System.out.println(url);
        }
    });
```
这样的代码是不能让人满意的，因为我们不能对数据流进行操作。如果我想要修改每个URL，我就只能在Subscriber中进行了。这里，我们竟然没有去使用如此酷的map()操作符！！！  
当然，我在这里可以使用map操作符，可以直接操作urls,但是处理的时候还是要for each遍历，同样很麻烦。

我们可以使用Observable.from()方法，它接收一个集合作为输入，然后每次输出一个元素给subscriber：
```
Observable.from("url1", "url2", "url3")
    .subscribe(url -> System.out.println(url));
```
这看起来不错，把它使用到刚才的地方：

```
query("Hello, world!")
    .subscribe(urls -> {
        Observable.from(urls)
            .subscribe(url -> System.out.println(url));
    });
```
这里虽然规避了for-each 循环，但是代码却是混乱的。我们现在有多个嵌套订阅，这不仅丑陋还难以修改，更糟糕的是它会破坏RxJava中的设计理念。

#### 更好的方式
这里我们可以使用flatMap。  
Observable.flatMap() 接收一个Observable的输出作为输入，同时返回另外一个Observable。你以为你会返回一个数据流，但是却是另外一个Observable。看下面的代码：
```
query("Hello, world!")
    .flatMap(new Func1<List<String>, Observable<String>>() {
        @Override
        public Observable<String> call(List<String> urls) {
            return Observable.from(urls);
        }
    })
    .subscribe(url -> System.out.println(url));
```
简化后：

```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .subscribe(url -> System.out.println(url));
```
flatMap()是不是看起来很奇怪？为什么它要返回另外一个Observable呢？理解flatMap的关键点在于，flatMap输出的新的Observable正是我们在Subscriber想要接收的。现在Subscriber不再收到List<String>，它得到是由observable.from（）返回的一系列单个字符串。

#### 更进一步
flatMap() 它可以返回任何它想返回的Observable对象。  
假如我又有另外一个方法：

```
// Returns the title of a website, or null if 404
Observable<String> getTitle(String URL);
```
接着前面的例子，现在我要打印收到的每个网站的标题。但这里有一个问题，我的方法每次只能传入一个URL而且返回值不是一个String，而是一个输出String的Observable对象。  
使用flatMap和容易解决这个问题，在将URL列表拆分为单个字符串后，我们在flatMap中使用getTitle() 然后传入Subscriber中：
```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(new Func1<String, Observable<String>>() {
        @Override
        public Observable<String> call(String url) {
            return getTitle(url);
        }
    })
    .subscribe(title -> System.out.println(title));
```
简化后：
```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(url -> getTitle(url))
    .subscribe(title -> System.out.println(title));
```
有点不可思议，我能将多个独立的返回Observable对象的方法组合在一起！  
不仅如此我还将两个API的调用组合到一个链式调用中了。我们同样能够将许多API的调用连接起来，大家应该都应该知道同步所有的API调用，然后将所有API调用的回调结果组合成需要展示的数据是一件多么痛苦的事情。  
现在所有的逻辑都包装成了这种简单的响应式调用。

#### 丰富的操作符
目前为止，我们只了解了两个操作符，RxJava中还有更多的操作符，那么我们如何使用其他的操作符来改进我们的代码呢？  
getTitle()返回null如果url不存在。我们不想输出"null"，那么我们可以从返回的title列表中过滤掉null值！

```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(url -> getTitle(url))
    .filter(title -> title != null)
    .subscribe(title -> System.out.println(title));
```
filter()输出和输入相同的元素，但是会过滤掉那些不满足检查条件的。  
现在我们只想最多显示5个结果：
```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(url -> getTitle(url))
    .filter(title -> title != null)
    .take(5)
    .subscribe(title -> System.out.println(title));
```
take()输出最多指定数量的结果。  
接着我们想把每个title保存在磁盘中：
```
query("Hello, world!")
    .flatMap(urls -> Observable.from(urls))
    .flatMap(url -> getTitle(url))
    .filter(title -> title != null)
    .take(5)
    .doOnNext(title -> saveTitle(title))
    .subscribe(title -> System.out.println(title));
```
doOnNext()允许我们在每次输出一个元素之前做一些额外的事情，比如这里的保存标题。  
看到这里操作数据流是多么简单了么。你可以添加任意多的操作而且不会搞乱你的代码。  
RxJava包含了大量的操作符很值得你去挨个看一下，这样你可以知道有哪些操作符可以使用。弄懂这些操作符可能会花一些时间，但是一旦弄懂了，你就完全掌握了RxJava的强大威力。

#### So What?
为什么要关心这些操作符？ 
因为操作符可以让你对数据流做任何操作。  

想象一下我们的数据在转换后是多么简单。在最后一个例子里，我们调用了两个API，对API返回的数据进行了处理，然后保存到磁盘。但是我们的Subscriber并不知道这些，它只是认为自己在接收一个Observable<String>对象。良好的封装性也带来了编码的便利！

[参考原文](https://blog.danlew.net/2014/09/22/grokking-rxjava-part-2/)