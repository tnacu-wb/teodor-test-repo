package uk.co.whitbread.payments.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class ThreeCResponseUtilTest {

    @Test
    void verifyCleanUpResponse() {
        String inputString = "\"{\\\"ipgSession\\\":\\\"6Y71NeaD1oEe0gP+Valdxfym1iyjao4xS645kSBp7FBLNSN6isjrBG/ofYERDWPWb5DiMCPsKwJxwx9cyv8/84g8nP+ZHPzdzDa2yztaC+RxVjMQ9yueK73a+qQzEwCxBXp4zN0Rs1ZXNAyBZpixbo/pFTw4C47PSx+csI1HmlcqVZiRo4Fuu8IoM2imMnDhGIf6Eyftg+Y7hKgynNCfRzBK3d71dwgG32/4aNWPbRhDm4n93RmoGqtRg3Amosho7WQ82kV6EOYk3jejH9cJU+qpWf7XqkjkbJdeZ5gw3m7SbxWcIbKWhlAywIP6PfyhBQyruhDGEJkxCzC61WZjTNCKduif3NBGcofV/5t34iII8DfHQp0wd7yVT3QtUm3qcUNV6jIgGeFIxkNtVMmWBSPiXikzDNY1BF+kZCVETqxXTmTxKecskPP6jt9mIFUD4DJyeAlCUzsqUJwTYapQVZK09XDt6ymp+nW/7+ac3EcGctFWyzgH4zMiEH45Ijn0b5wTR8/4RgC3GofPW9iQfG9DvNrdXKhHoOgKpm3jh0dc08Myq1xtqKQIS0WmKpxxROX5M+mbZUUvYTOs+EWuDeN1B+zZp/p1uRyMDH1EcLwYKvXisXd2LdwHKaaDg7siHimxdhfNcH5QmpTrKjmWFS0fYsArY6dWSQTfoyeD5R6Q2imdxoXwFqNeJfnbDwm7ISEW5ZZdFQsmaxg3hQnDLWamiIhOr/t6jxa8CF+/XahgNC1O82XBQBj0MeB1WgurFMIFFYbsFKcIwgsTXQTmXw7zZEFPIbAqrH5zoqGbMT39/Md2rVapSLe0Ljr9s+XIkaT6vYFh645aEp2rm92Gl1VJvUd8hQW8XLlnA1Axikw4ugfH6r2UlFRyQItJSKqL9bK3aQ+wC+J0hufT7YLw/k+7mYiwoxWgjsIInfqXFgsEY2t9R9bsv5Qyw8t6JECLlNtrLnxj63VCOTP8JzNu7Hv/5fpi6JnIxDlENjyX24HY6iy87FfXfle9xGcSPESaexxeBB3hIwfgrrI4kuvgXqg1fVSlRMi5nqvRdqE2fN818e8P6Z1QH68Mmmu2mGj2QTs8yra7Q/055OsVlq2lqZh5B59EZU7HhTXh364rHlMdR0iWlMYTOrENdMojWTuJydD6yeyuF+4kuMdK816dol+6304vCWxaQk3WYKzTEw/cHQKojPG0/ztPDyd6ZMBu4gS80OO6jTo=\\\",\\\"ipgResultCode\\\":0,\\\"ipgResultText\\\":\\\"Success\\\"}\"";
        String expectedString = "{\"ipgSession\":\"6Y71NeaD1oEe0gP+Valdxfym1iyjao4xS645kSBp7FBLNSN6isjrBG/ofYERDWPWb5DiMCPsKwJxwx9cyv8/84g8nP+ZHPzdzDa2yztaC+RxVjMQ9yueK73a+qQzEwCxBXp4zN0Rs1ZXNAyBZpixbo/pFTw4C47PSx+csI1HmlcqVZiRo4Fuu8IoM2imMnDhGIf6Eyftg+Y7hKgynNCfRzBK3d71dwgG32/4aNWPbRhDm4n93RmoGqtRg3Amosho7WQ82kV6EOYk3jejH9cJU+qpWf7XqkjkbJdeZ5gw3m7SbxWcIbKWhlAywIP6PfyhBQyruhDGEJkxCzC61WZjTNCKduif3NBGcofV/5t34iII8DfHQp0wd7yVT3QtUm3qcUNV6jIgGeFIxkNtVMmWBSPiXikzDNY1BF+kZCVETqxXTmTxKecskPP6jt9mIFUD4DJyeAlCUzsqUJwTYapQVZK09XDt6ymp+nW/7+ac3EcGctFWyzgH4zMiEH45Ijn0b5wTR8/4RgC3GofPW9iQfG9DvNrdXKhHoOgKpm3jh0dc08Myq1xtqKQIS0WmKpxxROX5M+mbZUUvYTOs+EWuDeN1B+zZp/p1uRyMDH1EcLwYKvXisXd2LdwHKaaDg7siHimxdhfNcH5QmpTrKjmWFS0fYsArY6dWSQTfoyeD5R6Q2imdxoXwFqNeJfnbDwm7ISEW5ZZdFQsmaxg3hQnDLWamiIhOr/t6jxa8CF+/XahgNC1O82XBQBj0MeB1WgurFMIFFYbsFKcIwgsTXQTmXw7zZEFPIbAqrH5zoqGbMT39/Md2rVapSLe0Ljr9s+XIkaT6vYFh645aEp2rm92Gl1VJvUd8hQW8XLlnA1Axikw4ugfH6r2UlFRyQItJSKqL9bK3aQ+wC+J0hufT7YLw/k+7mYiwoxWgjsIInfqXFgsEY2t9R9bsv5Qyw8t6JECLlNtrLnxj63VCOTP8JzNu7Hv/5fpi6JnIxDlENjyX24HY6iy87FfXfle9xGcSPESaexxeBB3hIwfgrrI4kuvgXqg1fVSlRMi5nqvRdqE2fN818e8P6Z1QH68Mmmu2mGj2QTs8yra7Q/055OsVlq2lqZh5B59EZU7HhTXh364rHlMdR0iWlMYTOrENdMojWTuJydD6yeyuF+4kuMdK816dol+6304vCWxaQk3WYKzTEw/cHQKojPG0/ztPDyd6ZMBu4gS80OO6jTo=\",\"ipgResultCode\":0,\"ipgResultText\":\"Success\"}";
        String outputString = ThreeCResponseUtil.cleanUpResponse(inputString);
        Assertions.assertEquals(expectedString, outputString);
    }

    @Test
    void verifyCleanUpResponseReturnsNullWhenEmpty() {
        String inputString = "";
        String outputString = ThreeCResponseUtil.cleanUpResponse(inputString);
        Assertions.assertNull(outputString);
    }

    @Test
    void verifyAddFormParamsToJsonNode() {
        var formParams = new HashMap<String, String>();
        formParams.put("foo", "bar");
        var objectNode = ThreeCResponseUtil.addFormParamsToJsonNode(formParams.entrySet());
        Assertions.assertTrue(objectNode.hasNonNull("foo"));
    }
}