package pp.rest_template.controller;

import pp.rest_template.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/*
Для работы предоставляется API по URL - http://94.198.50.185:7081/api/users
Ваша задача: Последовательно выполнить следующие операции (от 1 до 5) и получить код для проверки на платформе.
В результате выполненных операций вы должны получить итоговый код, сконкатенировав все его части.
Количество символов в коде = 18.
 */

@RestController
public class ConsumeWebService {
    final RestTemplate restTemplate;

    @Autowired
    public ConsumeWebService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public String getCode() {
        String requestUrl = "http://94.198.50.185:7081/api/users";
        String code = "";

//  №1 Получить список всех пользователей.
        
        ResponseEntity<List<User>> response = restTemplate.exchange(requestUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

//  №2 Когда вы получите ответ на свой первый запрос, вы должны сохранить свой session id, который получен через cookie.
//     Вы получите его в заголовке ответа set-cookie. Поскольку все действия происходят в рамках одной сессии, все
//     дальнейшие запросы должны исп. полученный session id (необходимо использовать заголовок в последующих запросах)

        String cookies = response.getHeaders().getValuesAsList("Set-Cookie").get(0);
        String jsessionid = cookies.substring(cookies.indexOf("JSESSIONID="), cookies.indexOf(";"));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Cookie", jsessionid);

//  №3 Сохранить пользователя с id = 3, name = James, lastName = Brown, age = на ваш выбор.
//      В случае успеха вы получите первую часть кода.

        User newUser = new User(3L, "James", "Brown", (byte)33);
        HttpEntity<User> entity = new HttpEntity<>(newUser, headers);
        ResponseEntity<String> response1 = restTemplate.exchange(requestUrl,
                HttpMethod.POST, entity, String.class);
        code += response1.getBody();

//  №4 Изменить пользователя с id = 3. Необходимо поменять name на Thomas, а lastName на Shelby.
//      В случае успеха вы получите еще одну часть кода.

        User user = new User(3L, "Thomas", "Shelby", (byte)33);
        HttpEntity<User> entity1 = new HttpEntity<>(user, headers);
        ResponseEntity<String> response2 = restTemplate.exchange(requestUrl,
                HttpMethod.PUT, entity1, String.class);
        code += response2.getBody();

//  №5 Удалить пользователя с id = 3. В случае успеха вы получите последнюю часть кода.

        HttpEntity<User> entity2 = new HttpEntity<>(headers);
        ResponseEntity<String> response3 = restTemplate.exchange(requestUrl + "/3",
                HttpMethod.DELETE, entity2, String.class);
        code += response3.getBody();
        System.out.println("CODE - " + code);
        return code;
    }

}
