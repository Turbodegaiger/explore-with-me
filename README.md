# java-explore-with-me
Project Explore With Me is made for planning where to go and for posting new events.
Проект Explore With Me создан для планирования куда бы сходить и для размещения новых событий.
Использованы технологии: Spring Boot, Spring Data, Spring MVC, Apache Maven, Docker, PostgreSQL, H2, Hibernate, QueryDSL, Lombok.

По сути можно назвать проект онлайн-афишей.
Позволяет авторизованным пользователям создавать новые события, оценивать их, искать что-то для себя, оставлять заявки на участие в событии.
Для неавторизованных пользователей доступен поиск событий по множеству параметров, а также подборок событий.
Для администраторов предусмотрена возможность создания подборок, категорий, а также модерации событий и пользователей.
Также приложение хранит статистику посещений, просмотров событий.

Для запуска приложения потребуется установленный Docker.
Структуру можно посмотреть в файле docker-compose.yml в корневой папке.
При помощи терминала необходимо создать docker image для main-service, stats-server и stats-client, после чего командой "docker-compose up" запустить приложение.
Образы также можно скачать с docker hub при помощи команд: "docker pull tbdggr/ewm-service-image", "docker pull tbdggr/stats-server-image", "docker pull tbdggr/stats-client-image".
