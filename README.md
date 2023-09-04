# SpringBoot + 自定义注解，实现用户操作日志(支持SpEL表达式)

## 背景

一个成熟的系统，都会针对一些关键的操作,去创建用户操作日志。

比如:

```
XX人创建了一条订单,订单号:XXXXXXXXX
```

因为`操作人`或者`订单号`是动态的，所以有些开发人员,不知道获取，就将这种操作日志和业务代码融在一起。

我们当然要杜绝这种现象，一定会有更好的解决方案。

当前项目除了满足上面这个基础需求场景外，还可以满足一些常见的日志记录需求。

下面通过一些测试用例来了解下当前项目吧。


## 一、支持SpEL表达式

#### 1、使用场景

我们在记录操作日志的时候，为了获取接口中的入参信息，就可以通过SpEL表达式。

#### 2、接口示例

```java
 @GetMapping(value = "/queryUser")
 @OperateLog(bizNo = "{#userName}", operateName = "查询用户", operateContent = "通过 {#userName} 查询用户")
 public Result<String> queryUser(@RequestParam String userName) {
        return Result.success(userName);
}
```

这里入参 `userName` 的获取，就是通过SpEL表达式获取到

#### 3、请求接口

```
http://localhost:8080/queryUser?userName=James
```

#### 4、输出日志

```
日志操作记录------ OperateLogDTO(operator=张三, bizNo=James, operateName=查询用户, operateContent=通过 James 查询用户, status=true, errMsg=null)
```

可以看出通过SpEL表达式，已经将 `{#userName}` 占位符，成功替换请求的参数 `James`。

<br>

## 二、支持函数表达式

#### 1、使用场景

有些时候我们仅仅是获取请求参数的数据还不够，还需要拿着请求参数的数据，去请求其它接口,才能组成一条完整的日志，最典型的场景就是

```
商品ID为 XXX 的商品的名称已经从 XXX  改成 XXX
```

这里前端肯定只会传当前的商品ID和当前修改后的商品名称,也就是说该商品老的名称还需要通过商品ID去商品表查完后，才能组成完整日志。

#### 2、接口示例

```java
@GetMapping(value = "/deleteUser")
@OperateLog(bizNo = "{#userId}", operateName = "删除用户",
        operateContent = "用户id为 {#userId} 用户名为 [getUserNameByUserId{#userId}] 已被删除")
public Result<Void> deleteUser(Long userId) {
        return Result.success();
        }
```
#### 3、请求接口

```
http://localhost:8080/deleteUser?userId=888
```

#### 4、输出日志

```
日志操作记录------ OperateLogDTO(operator=张三, bizNo=888, operateName=删除用户, operateContent=用户id为 888 用户名为 张老三 已被删除, status=true, errMsg=null)
```

这里用户名`张老三`,是模拟查询数据库获取的用户名。

<br>

## 三、支持三目表达式

#### 1、使用场景

有时候我们可能根据是否传主键id来判断是新增还是更新，或者传不同的type来确定什么操作类型。

#### 2、接口示例

```java
@PostMapping(value = "/saveOrUpdateUser")
@OperateLog(bizNo = "{#dto.userId}", operateName = "#dto.userId == null ? '新增用户':'更新用户'",
        operateContent = "#dto.userId == null ? '新增' + #dto.userName + '用户':'将用户id为' + #dto.userId + '的用户名更新为' + #dto.userName")
public Result<Void> saveOrUpdateUser(@RequestBody UserDTO dto) {
        return Result.success();
}
```

#### 3、请求示例

`请求url`

```
localhost:8080/saveOrUpdateUser
```

`请求参数`

```json
{
  "userId": 17,
  "userName": "赵磊"
}
```

#### 4、输出日志

传入userId的日志

```
日志操作记录------ OperateLogDTO(operator=张三, bizNo=null, operateName=更新用户, operateContent=将用户id为17的用户名更新为赵磊, status=true, errMsg=null)
```

如果不传入userId只传userName，我们再看下日志

```
日志操作记录------ OperateLogDTO(operator=张三, bizNo=null, operateName=新增用户, operateContent=新增赵磊用户, status=true, errMsg=null)
```

<br>

## 四、支持标记成功日志或业务异常日志

#### 1、使用场景

请求一个接口的时候,可以分为三种情况:

- 接口返回成功，同时返回成功状态码
- 接口返回成功，但返回业务异常状态码,比如:没有查询到该订单信息
- 接口直接报错,比如我们常见的空指针异常

对于第三种情况，我们可以不去记录这个用户操作日志，如果有需要可以记录在异常记录表中。

但对于第一和第二种情况，我们需要记录操作是成功还是失败。

#### 2、接口示例

```java
@PostMapping(value = "/saveUser")
@OperateLog(bizNo = "{#dto.userId}", operateName = "新增用户", operateContent = "新增用户名为{#dto.userName}")
public Result<Void> saveUser(@RequestBody UserDTO dto) {
        return Result.failed("该用户名称已存在");
        }
```

可以看出接口返回的是 `该用户名称已存在`,业务异常。

#### 3、请求示例

`请求url`

```
localhost:8080/saveUser
```

`请求参数`

```json
{
  "userId": 2,
  "userName": "阎平"
}
```

#### 4、输出日志

```
日志操作记录------ OperateLogDTO(operator=张三, bizNo=2, operateName=新增用户, operateContent=新增用户名为阎平, status=false, errMsg=该用户名称已存在)
```

可以看出这里的status状态是 `false`，同时记录了错误原因 `该用户名称已存在`。



