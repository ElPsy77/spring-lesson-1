package kz.iitu.springlab.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

// @RestController = @Controller + @ResponseBody: Spring создаёт бин-контроллер,
// а результат каждого метода автоматически сериализуется в тело ответа (JSON),
// а не трактуется как имя view-шаблона.
@RestController
// @RequestMapping("/api") задаёт общий префикс пути для всех эндпоинтов
// этого контроллера — итоговые адреса будут /api/hello, /api/info, /api/sum.
@RequestMapping("/api")
public class HelloController {

    // @Value("${app.owner:unknown}") — внедряет значение свойства app.owner
    // из application.properties. Часть после двоеточия (unknown) — значение
    // по умолчанию, которое подставится, если свойство app.owner не задано.
    @Value("${app.owner:unknown}")
    private String owner;

    // @GetMapping("/hello") — сокращение для @RequestMapping(method = GET, path = "/hello").
    // Полный путь с учётом префикса класса: GET /api/hello.
    @GetMapping("/hello")
    public Greeting hello(
            // @RequestParam(defaultValue = "world") — читает query-параметр ?name=...
            // из URL. Если параметр не передан, используется значение "world",
            // поэтому запрос без параметра не приводит к ошибке.
            @RequestParam(defaultValue = "world") String name
    ) {
        // Формируем сообщение и возвращаем record — Spring сам сериализует его в JSON.
        String message = "Hello, " + name + "!";
        return new Greeting(message, owner, LocalDateTime.now());
    }

    // GET /api/info — возвращает информацию о владельце и рантайме приложения.
    @GetMapping("/info")
    public Info info() {
        // System.getProperty("java.version") — версия JVM, на которой запущено приложение.
        String javaVersion = System.getProperty("java.version");
        // Runtime.getRuntime().availableProcessors() — число ядер CPU, доступных JVM.
        int cpuCores = Runtime.getRuntime().availableProcessors();
        return new Info(owner, javaVersion, cpuCores);
    }

    // GET /api/sum?a=..&b=.. — принимает два целых числа и возвращает их сумму,
    // разность и произведение.
    @GetMapping("/sum")
    public SumResult sum(
            // defaultValue = "0" — если параметр a отсутствует в запросе,
            // Spring подставит строку "0" и сконвертирует её в int,
            // поэтому запрос без параметра не вызывает ошибку 400.
            @RequestParam(defaultValue = "0") int a,
            // Аналогично для параметра b.
            @RequestParam(defaultValue = "0") int b
    ) {
        int sum = a + b;
        int difference = a - b;
        int product = a * b;
        return new SumResult(a, b, sum, difference, product);
    }

    // record — неизменяемый класс-данные: Java сама генерирует конструктор,
    // геттеры (message(), owner(), timestamp()), equals/hashCode/toString.
    // Jackson (сериализатор JSON, встроенный в Spring Boot) превращает
    // такой record в JSON-объект по именам компонентов.
    public record Greeting(String message, String owner, LocalDateTime timestamp) {
    }

    // record с информацией о приложении/рантайме.
    public record Info(String owner, String javaVersion, int cpuCores) {
    }

    // record с результатом арифметических операций над a и b.
    public record SumResult(int a, int b, int sum, int difference, int product) {
    }
}
