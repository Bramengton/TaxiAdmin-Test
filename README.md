# TaxiAdmin-Test
Тестовое задание на вакансию Android developer в TaxiAdmin

Что было сделано:
Для парсинга использовалась библиотека <a href=https://jsoup.org/download>Jsoup</a><br>compile 'org.jsoup:jsoup:1.11.2'<br>
Запуск парсера происходит циклически, цикл 3 сек, согласно тех заданию.
Также в приложении проверяются права на использование доступов:
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

Так как в процессе разработки выяснилось что сервер не возвращает события "pr_order", это привело к тому что 
закаладка "Предв." - пуста, проверить её работоспособность возможности нет.

Переключение закладок, вращение экрана - работают. Сохранение состояния - работает.
Оповещения - беззвучны.
