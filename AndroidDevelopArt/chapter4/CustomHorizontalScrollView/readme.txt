
book：对于自定义ViewGroup没什么参考价值，不是一个合格的自定义ViewGroup，也许对于滑动冲突有点价值。

isure: 我自己根据对自定义View Group的理解，以及参考LinearLayout\FrameLayout源码实现，可以算是一个合格的自定义ViewGroup，相比google模块还有需要完善的地方。

google:是google提供的自定义ViewGroup的demo，官方、权威、简洁，自定义ViewGroup该有的功能完善，不相干的功能没有。
我是从ViewGroup的注释中找到，由于activity_main.xml中需要的图片没有，所以效果可能受到影响，因为控件的背景图片是会影响控件的测量尺寸的。