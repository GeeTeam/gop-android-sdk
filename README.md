# gop-android-sdk

# 概述与资源

Android SDK提供给集成Android原生客户端开发的开发者使用。

## 环境需求

条目	|资源 			
------	|------------	
开发目标|4.0以上	
开发环境|Android Studio 2.1.3
系统依赖|`v7包`
sdk三方依赖|无	

# 安装

## 获取SDK

1. 在demo的`libs`包下，将获取的`.aar`文件拖拽到工程中的libs文件夹下。

2. 在拖入`.aar`到libs文件夹后, 还要检查`.aar`是否被添加到**Library**,要在项目的
build.gradle下添加如下代码：

```java
repositories {
	flatDir {
  		dirs 'libs'
	}
}

```

并且要手动将aar包添加依赖：

```java
compile(name: 'geetest_message_android_v1.x.y', ext: 'aar')

``` 

## 配置接口

开发者集成客户端sdk前, 必须先在您的服务器上搭建相应的**服务端SDK**。

集成用户需要使用Android SDK完成提供的以下接口:

1. 配置并初始化
2. 调用校验接口
3. 处理结果
4. 处理错误

>集成代码参考下方的**代码示例**

## 编译并运行你的工程

编译你的工程, 体验全新的极验onepass产品！

# 代码示例

## 初始化与校验

### 初始化

在项目的具体页面的`onCreate`方法里面进行初始化。
	
```java
gopGeetestUtils = GOPGeetestUtils.getInstance(MainActivity.this);

``` 

### 点击执行

```java
gopGeetestUtils.getOnePass(MainActivity.this, parmas, editText.getText().toString(),CUSTOM_ID,gtmLinster);
//第一个参数为上下文
//第二个参数为要传给sdk的请求数据
//第三个参数为输入的手机号码
//第四个参数为所需要配置的CUSTOM_ID
//第五个参数为所需接口

``` 
 
### 接口实现

实现接口进行校验。

```java
GOPUtilsListener gopLinster=new GOPGeetestUtils.GOPUtilsListener() {
	@Override
	public void gopOnReady(boolean result) {
		//自定义加载动画的开关。当为ture的时候表示验证码准备显示出来，此时自定义loading进行关闭；当为false的时候，验证码验证成功，继续自定义loading展示。
	}

	@Override
	public void gopOnError(String error) {
		//过程中出现的错误
	}

	@Override
	public void gopOnSendMsg(boolean canSendMSG，Map<String, String> result) {
		//sdk内部发送短信所需要的结果，当为true的时候表示sdk内部发送短信，false的时候自定义短信
	}

	@Override
	public void gopOnResult(Map<String, String> result) {
		//网关校验拿到的结果，自定义进行校验
	}
};
``` 
额外接口实现。

```java
   gopOnCancelDialog():关闭验证码的接口
   gopOnCloseDialog();点击页面关闭键取消验证码的接口
   gopOnLanguage();验证码的语言，默认为系统语言
   gopOnChangePhone();当为true的时候，表示由sdk内部判断手机号格式，false为外部判断。默认为false
   gopOnDobble();此接口用于未收到短信，进行再次请求时调用，默认为false
   
``` 
### 页面关闭

在页面关闭的时候执行此方法。

```java
@Override
protected void onDestroy() {
 	super.onDestroy();
 	gopGeetestUtils.cancelUtils();
}

``` 

# SDK方法说明

## 获取实例对象

### 方法描述

获取管理类的实例对象

	
```
public GOPGeetestUtils(Context context)
```

### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
context|Context|上下文|	

## 获取校验结果

### 方法描述

costomID：产品id，请在官网申请

configUrl:初始化接口，网站主使用onepass的服务端sdk搭建

```
public void getOnePass(Context context，String params，String phone,String customID,GOPListenerUtils gopListener)
```

### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
context|Context|activity的上下文|
params|String|configUrl的请求结果|
phone|String|用户所填的手机号|
customID|String|产品id|
gopListener| GOPListenerUtils|回调监听器，需要开发者自己实现|

### 代码示例

```
gopGeetestUtils.getOnePass(MainActivity.this,params,phone,customid,gopListener)
```

## 回调监听

verifyUrl：onepass校验接口，网站主使用onepass的服务端sdk搭建

checkMessageUrl：onepass校验接口，网站主使用onepass的服务端sdk搭建

### 错误回调

#### 方法说明

整个流程出现错误的时候调用
	
```
public gopOnError(String error)

```

#### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
error | String |错误码|	

### 处理网关参数回调

#### 方法说明

整个流程网关成功之后调用
	
```
public gopOnResult(Map<String,String> result)

```

#### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
result | Map |verifyUrl的请求参数|

#### verifyUrl的请求参数说明

key	|说明| 			
------	|-----|
`custom`|产品id|
`process_id`|流水号|
`phone`|手机号|
`accesscode`|网关token，从运营商获取|
`clienttype`|系统类型，1表示为Android|

#### verifyUrl的请求结果说明

参数	|类型|说明| 			
------	|---|-----|
result|int|当等于0的时候表示成功，2的时候表示需要提交费用，其他表示验证手机号失败|

### 处理短信参数回调

#### 方法说明

整个流程进行发送短信调用
	
```
public gopOnSendMsg(boolean data，Map<String,String> result)

```

#### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
data|boolean|客户所选择的短信发送业务，如果为false，则自定义短信发送，如果为true，则表示短信业务由onepass sdk内部发送|
result | Map |checkMessageUrl的请求参数|

#### checkMessageUrl的请求参数说明

key	|说明| 			
------	|-----|
`custom`|产品id|
`process_id`|流水号|
`phone`|手机号|

#### checkMessageUrl的请求结果说明

参数	|类型|说明| 			
------	|---|-----|
result|int|当等于0的时候表示成功，2的时候表示需要提交费用，其他表示验证验证码失败|

## 关闭验证

### 方法描述

在activity的onDestroy()方法中实现

```
public void cancelUtils()
```

### 参数说明

无

### 代码示例

```
gopGeetestUtils.cancelUtils()
```	

# 常用错误码

error	|说明| 			
------	|-----|
`211`|configUrl出错|
`200`|ajax forbidden|
`201`|验证timeout|
`204`|webview error|


	
