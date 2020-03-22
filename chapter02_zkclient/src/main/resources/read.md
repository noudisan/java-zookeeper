zk-client

支持同步创建。
更丰富更简单的序列化方式，原始的只能传递byte[]数组。
更简便的API，createPersistent和createEphemeral等。
支持递归创建。


Zookeeper原生API接口的基础上进行了包装，内部实现了Session超时重连，Watcher反复注册等功能。

解决了Session超时重连
Watcher反复注册等功能。
更丰富的序列化方式，ZKClient允许用户自行注册序列化器，然后客户端在进行读写操作过程中，就会自动进行系列化和反序列化操作，默认情况下，ZKClient使用Java自带的序列化方式对对象进行序列化。