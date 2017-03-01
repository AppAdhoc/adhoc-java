发布到Maven上的步骤:

1. 把董源贵的pubring.gpg和secring.gpg放在~/.gnupg目录下
2. sbt publishSigned
    输入gpg的口令
3. sbt sonatypeRelease

注意更新版本信息, 当前是0.0.10
https://repo1.maven.org/maven2/com/appadhoc/adhoc-java/0.0.10/