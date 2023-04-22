import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class test {

    public static String authorization;
    public static String idProduto;

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
    void cadastrarUsuariosPost(){

        JSONObject requestParams = new JSONObject();
        requestParams.put("nome", "Caio Augusto");
        requestParams.put("email", "teste2@outlook.com");
        requestParams.put("password", "teste");
        requestParams.put("administrador", "true");

        //pre-condições ficam no given como body
        given()
                .contentType("application/json")
                .body(requestParams.toString())
                .when()
                    .post("http://localhost:3000/usuarios")
                .then()
                .statusCode(201)
                .log().all()
                .body("message", equalTo("Cadastro realizado com sucesso"))
                .body("_id", notNullValue());
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

    @Test
    void cadastrarProdutoPost(){

        JSONObject requestBody = new JSONObject();
        realizarLoginPost();
        System.out.println(authorization);

        requestBody.put("nome", "teste4");
        requestBody.put("preco", 5000);
        requestBody.put("descricao", "Celular");
        requestBody.put("quantidade", 30);

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
        System.out.println(idProduto);

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

    // colocar faker
    // testar todos os end=poits de uma só vez
    // melhorar o codigo abstraindo
    // colocar os testes
    // reportar















}

