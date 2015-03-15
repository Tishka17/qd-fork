**Требуемое программное обеспечение:**
  * Последняя ревизия BombusQD
  * Java SE JDK (http://java.sun.com/javase/)
  * Subversion client (http://subversion.tigris.org)
  * Ant (http://ant.apache.org/)
  * Android SDK 20 версии и выше с загруженным Android 1.6 api (http://developer.android.com/)
  * Netbeans (http://www.netbeans.com/downloads/index.html)
А так же потребуется персональный ключ разработчика для подписи приложения


_**Процесс сборки:**_

**1.** В Netbeans'e добавляем проект BombusQD

**2.** Открываем в исходниках _BombusQD_ папку _android_
создаем в ней копию файлоа **`_local.properties`**
называя **`local.properties`** соответственно.
В корне BombusQD создаем копию файла **`_build.cmd`** и называем его
**`build.cmd`**

**3.** Редактируем файл **build.cmd** любым текстовым редактором,
указывая в нем верные пути к JDK и Netbeans

пример:
```
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_21\
set ANT_EXEC="C:\Program Files\NetBeans 7.0.1\java\ant\bin\ant.bat"
```
**4.** Редактируем файл **local.properties** так же любым текстовым редактором

пример:
```
sdk.dir=D:/Android/android-sdk-windows  ///путь к Android-sdk
key.store=D:/keys/key/mykey.keystore    ///путь к хранилищу ключей
key.alias=cert                          ///имя хранилища
key.store.password=pass                 ///пароль от хранилища
key.alias.password=pass                 ///пароль от ключа

```

**5.** Возвращаемся в корень исходников BombusQD. Для простоты сборки создаем в ней **Новый текстовый документ.txt**
переименовываем его в любой файл с расширением **.bat**, например **android.bat**
Редактируем его с помощью любого текстового редактора и пишем следующее
```
call ant build-android
pause
```
Теперь все готово для сборки андроид-версии, запускаем наш bat-файл и наблюдаем за процессом компиляции

**6.** Наши собранные apk файлы после компиляции будут располагаться в папке **android/bin/**
```
BombusQD-release.apk  ///наше подписанное приложение для установки в телефон
BombusQD-unaligned.apk  ///подписано сертификатом, но не выровнено
BombusQD-unsigned.apk  ///без подписи
```
# Внимание #
Собранное Вами приложение не установится поверх того, что выкладывается для скачивания