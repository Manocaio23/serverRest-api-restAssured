import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import com.github.javafaker.Faker;


import java.util.List;


public class test {

    //variaveis estaticas para pegar valores
    public static String authorization;
    public static String idProduto;

    private static String idUsuario;
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();

    @Test
    void test1(){
   Response response=  get("http://localhost:3000/");
   System.out.println("Status: "+response.getStatusCode());
    }

    @Test
    void listarUsuariosGet(){

        given()
                .when().get("http://localhost:3000/usuarios")
                .then().log().all();
    }

    @Test
    void listarUsuariosGetId(){

        given()
                .pathParam("id", idUsuario)
                .when()
                .get("http://localhost:3000/usuarios/{id}")
                .then()
                .statusCode(200)
                .log().all()
                .body("_id", equalTo(idUsuario));


    }

    @Test
    void cadastrarUsuariosPost(){

        JSONObject requestParams = new JSONObject();
        requestParams.put("nome", "Caio Augusto");
        requestParams.put("email", email);
        requestParams.put("password", "teste");
        requestParams.put("administrador", "true");

        //pre-condições ficam no given como body
        Response response = given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                    .post("http://localhost:3000/usuarios")
                .then()
                .statusCode(201)
                .log().all()
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue())
                .extract()
                .response();

        idUsuario = response.path("_id");

       ;

        listarUsuariosGetId();

        System.out.println("Id do usuário cadastrado: "+idUsuario);


    }

    @Test
    void realizarLoginPost(){

        JSONObject requestBody = new JSONObject();

        requestBody.put("email", "teste2@outlook.com");
        requestBody.put("password", "teste");

        Response response = given()
                .contentType("application/json")
                .body(requestBody.toString())
                .when()
                .post("http://localhost:3000/login")
                .then()
                .statusCode(200)
                .log()
                .all()
                .extract()
                .response();


        authorization = response.jsonPath().get("authorization");
        authorization = authorization.substring(7);
        System.out.println(authorization);
    }

    // outro login pegando json de outra classe

    @Test
    void realizarLoginPostBody(){

        JSONObject requestBody = jsonUtils.getLoginRequestBody();


        Response response = given()
                .contentType("application/json")
                .body(requestBody.toString())
                .when()
                .post("http://localhost:3000/login")
                .then()
                .statusCode(200)
                .log()
                .all()
                .extract()
                .response();


        authorization = response.jsonPath().get("authorization");
        authorization = authorization.substring(7);
        System.out.println(authorization);
    }

    @Test
    void cadastrarProdutoPost(){

        JSONObject requestBody = new JSONObject();
        realizarLoginPost();
        System.out.println(authorization);


        requestBody.put("nome", faker.commerce().productName());
        requestBody.put("preco", faker.number().randomNumber(3, false));
        requestBody.put("descricao", faker.lorem().sentence());
        requestBody.put("quantidade", faker.number().randomNumber());

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authorization)
                .body(requestBody.toString())
                .when()
                .post("http://localhost:3000/produtos")
                .then()
                .log()
                .all()
                .statusCode(201)
                .extract()
                .response();


        idProduto = response.jsonPath().get("_id");
        System.out.println("id do produto: "+idProduto);



    }

    @Test
    void consultarProdutoRecemCriadoPorIdGet(){

        realizarLoginPost();
        cadastrarProdutoPost();

        given()
                .when().get("http://localhost:3000/produtos/" +idProduto)
                .then().log().all();

    }

    @Test
    void consultarProdutoIdGet(){

        given()
                .when().get("http://localhost:3000/produtos/" +idProduto)
                .then().log().all();

    }

    @Test
    void apagarProdutos() {


        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + authorization)
                .when()
                .get("http://localhost:3000/produtos")
                .then()
                .statusCode(200)
                .extract()
                .response();

        List<String> produtosIds = response.jsonPath().getList("produtos._id");

        for (String id : produtosIds) {
            realizarLoginPost();
            given()
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + authorization)
                    .when()
                    .delete("http://localhost:3000/produtos/" + id)
                    .then()
                    .statusCode(200);
            System.out.println("Produto com id " + id + " apagado com sucesso");
        }
    }





    // testar todos os end=poits de uma só vez
    // melhorar o codigo abstraindo
    // colocar os testes
    //ham crest
















}

