# util.selenium.web-capture
"util.selenium.web-capture"は、Webページのキャプチャツールです。
## Prerequisites
事前に準備すること
* Java Development Kit 8 or newer
* Gradleをインストール
* キャプチャで使用するブラウザをインストールし、selenium web driverへのパスが通っていること

## Building
1. gitリポジトリをローカルにcloneします。

        git clone https://github.com/Yanmo/util.selenium.web-capture.git

1. cloneしたローカルのgitリポジトリに移動します。

        cd util.selenium.web-capture

1. rootディレクトリでGradleコマンドを起動して、javaコードと必要なライブラリをビルドします。

        ./gradlew

## Usage
まだ開発途中なので、ビルドしたjarファイルでのテストはできていません。ecliseのデバッグモードで起動してください。
対応しているブラウザは、chrome、firefox、internet explorer、edge、safariです。

使用できるコマンドラインオプションは以下の通りです。

|オプション名|説明|
|---|---|
|-i|入力するwebページを指定します。 |
|-o|キャプチャファイルを保存する場所を指定します。|
|-b|キャプチャで使用するブラウザを指定します。複数使用するときは":"で区切ってください。|
|-driver|ローカル環境で使用するときに、selenium-web-driverの保存先を指定します。|
|-remote|selenium-remote-web-driverを使用するときに指定します。|
|-js|キャプチャする直前に実行するjavascriptファイルを指定します。|
|-w|キャプチャするときのウィンドウのwidthを指定します。指定しないときは1200pxでキャプチャします。|
|-h|キャプチャするときのウィンドウのheightを指定します。指定しないときは768pxでキャプチャします。|

### 保存するキャプチャ画像のファイル名について
<キャプチャしたhtmlファイルのファイル名>\_<言語>\_<ブラウザ>\_< width x height >.png

になります。

### ローカル環境で実行するときのコマンドライン例
下記を参考に、Eclipseのデバッグモードでコマンドラインオプションを指定してください。

        java -jar util.selenium.web-capture-0.1.jar -i http://localhost/index.html -o C:\capture -l JA -driver C:\selenium-web-driver\bin -b chrome:edge:firefox:safari:ie -w 1600 -h 1200

### selenium-remote-web-driverで実行するときのコマンドラインオプション
下記を参考に、Eclipseのデバッグモードでコマンドラインオプションを指定してください。

        java -jar util.selenium.web-capture-0.1.jar -i http://localhost/index.html -o C:\capture -l JA -remote http://localhost:4444/wd/hub -b chrome:edge:firefox:safari:ie -w 1600 -h 1200
