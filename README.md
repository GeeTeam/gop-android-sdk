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
compile(name: 'geetest_onepass_android_vx.y.z', ext: 'aar')

``` 

3. 添加权限

```java
 <uses-permission android:name="android.permission.WRITE_SETTINGS" />
 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />

```

## 配置接口

开发者集成客户端sdk前, 必须先在您的服务器上搭建相应的**服务端SDK**，生成二次验证的接口即下文的**verifyUrl**。

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
gopGeetestUtils.getOnePass( editText.getText().toString(),validate,CUSTOM_ID,gopLinster);
//第一个参数为输入的手机号码
//第二个参数为接入验证码SDK返回的validate(如果只接入onepass则传null)
//第三个参数为所需要配置的CUSTOM_ID
//第四个参数为所需接口

``` 
 
### 接口实现

实现接口进行校验。

```java
BaseGOPListener gopLinster=new BaseGOPListener() {
	@Override
	public void gopOnError(String error) {
		//过程中出现的错误
	}

	@Override
	public void gopOnSendMsg(boolean success,Map<String, String> result, JSONObject jsonObject) {
		//sdk内部发送短信所需要的结果，当为true的时候表示sdk内部发送短信，false的时候自定义短信
	}

	@Override
	public void gopOnResult(String result) {
		//校验成功,返回校验成功数据
	}

	@Override
    public String gopOnVerifyUrl() {
    	//接入服务端SDK生成二次验证接口接口,
    	return GOP_VERIFYURL;
    }
};

	@Override
    public Map<String, String> gopOnVerifyUrlBody() {
        // verifyUrl接口传入form数据对象
        HashMap<String, String> map = new HashMap<>();
        // map.put("test","test");
        return null;
    }

	@Override
    public Map<String, String> gopOnVerifyUrlJsonBody() {
    	// verifyUrl接口传入json数据对象
    	HashMap<String, String> map = new HashMap<>();
    	// map.put("test","test");
    	return null;
    }

    @Override
    public Map<String, String> gopOnVerifyUrlHeaders() {
        // verifyUrl接口传入header对象
        HashMap<String, String> map = new HashMap<>();
        // map.put("Content-Type","application/json;charset=UTF-8");
        map.put("Content-Type", "application/x-www-form-urlencoded");
        return null;
    }
``` 
额外接口实现。

```java
   gopOnDobble();此接口用于未收到短信，进行再次请求时调用,默认为false。
   gopOnDefaultSwitch();此接口用于判断是否调用本sdk内置短信,默认为false。
   gopOnVerifyUrlHeaders;此接口用于向verifyUrl接口传递header,默认为null。
   gopOnVerifyUrlBody();此接口用于向verifyUrl的接口body中传参,默认为null。
   gopOnVerifyUrlJsonBody;此接口用于向verifyUrl的接口body传参，提供，默认为null。
   gopOnAnalysisVerifyUrl();此接口用于拿到校验的接口返回的参数,并获取返回值回传给sdk。

``` 
``` 
注意：verifyUrl接口只支持post，兼容form和json数据格式上行数据。

Form：重写gopOnVerifyBody方法，返回上行body参数。如果没有参数则返回为null或者未put数据的map，注意gopOnVerifyUrlJsonBody方法返回未null或者不重写
Json：重写gopOnVerifyUrlJsonBody方法，返回上行body参数，如果没有需要传输参数则返回未put数据的map。注意此时gopOnVerifyBody方法返回null或者不重写

默认上行body参数包括phone，process_id，accesscode，custom等，不得在gopOnVerifyUrlBody和gopOnVerifyUrlJsonBody方法重复传入

gopOnVerifyUrlHeaders方法传入verifyUrl接口需要的header参数
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

```
public void getOnePass(String phone,String validate,String customID,BaseGopListener gopListener)
```

### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
phone|String|用户所填的手机号|
validate|String|验证码SDK返回的validate，如果未接验证码则为null|
customID|String|产品id|
gopListener| BaseGopListener|回调监听器，需要开发者自己实现|

### 代码示例

```
gopGeetestUtils.getOnePass(phone,validate，customid,gopListener)
```


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

### 网关成功回调

#### 方法说明

整个流程网关成功之后调用
	
```
public gopOnResult(String result)

```

#### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
result | String |verifyUrl的验证成功的结果|

### verifyUrl传入回调

#### 方法说明

回传verifyUrl

```
public String gopOnVerifyUrl()

```

### 处理短信参数回调

#### 方法说明

整个流程进行发送短信调用
	
```
public gopOnSendMsg(boolean data，Map<String,String> result, JSONObject jsonObject)

```

#### 参数说明

参数	|类型 |说明| 			
------	|-----|-----|
data|boolean|客户所选择的短信发送业务，如果为false，则自定义短信发送，如果为true，则表示短信业务由onepass sdk内部发送|
result | Map |checkMessageUrl的请求参数|
jsonObject|JSONObject|发送短信的原因,有token fail和verify fail两种|

#### checkMessageUrl的请求参数说明

key	|说明| 			
------	|-----|
`custom`|产品id|
`process_id`|流水号|
`phone`|手机号|
`message_id` |发送短信的id|
`message_number`|短信验证码|

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
### 方法描述 
获取SDK版本号

```
public void getVersion()
```

### 参数说明

无

### 代码示例

```
gopGeetestUtils.getVersion()
```	

## 混淆规则

```
-dontwarn com.geetest.onepass.**
-dontwarn com.geetest.encryption.**
-keep class com.geetest.onepass.** {
*;
}
-keep class com.geetest.encryption.** {
*;
}
```
## 日志打印

SDK提供部分日志，TAG为Geetest_GOP。

## ErrorCode

### OnePass

`OnePass`产品的错误代码

ErrorCode	|Description
----------|------------
231       |网络未连接
235       |sendmessage接口返回为null
237       |sendmessage接口错误
238       |账号需充值
239       |pregateway接口返回为null
240       |pregateway接口错误
242       |validate为null
243       |customID为null
245       |phone为null
251       |Get CM token fail(获取移动token失败)
252       |Get CU token fail(获取联通token失败)
253       |Get CT token fail(获取电信token失败)
254       |CM verify fail(移动verifyUrl接口验证失败)
255       |CU verify fail(联通verifyUrl接口验证失败)
256       |CT verify fail(电信verifyUrl接口验证失败)
261       |gopOnVerifyUrl接口未进行传值

## 常见错误

### 1.总是报251，或者252，或者253错误？

答：第一步：检查手机是否停机；第二步：若总是报251错误，检查测试apk的签名是否与在极验后台设置签名一致。总是报252错误请联系我们。总是报253错误请检查是否是2、3G网络联网（电信不支持2、3G网络进行网关验证）。

### 2.总是报240错误？

答：检查customId或者validate是否正确配置。

### 3.总是报254，或者255，或者256错误？

答：第一步：检查是否是本机号验证，检查是否是验证的手机号开启网络，确认是否是真机测试，确认verifyUrl接口为Post接口；第二步：检查verifyUrl接口是否配置正确（TAG为Geetest_GOP的Log可以看到是否成功）；第三步：打印gopOnAnalysisVerifyUrl回调的值，如果为0则成功，如果为1则失败，如果失败说明客户自己服务端接入失败，请客户服务端排查问题；第四步：如果gopOnAnalysisVerifyUrl未回调日志请参考demo打印日志。








	
