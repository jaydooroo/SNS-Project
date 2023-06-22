package edu.byu.cs.tweeter.client;

import android.os.FileUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;

public class PostStatusTest {

    private RegisterRequest request;
    private User expectedResponse;

    @BeforeEach
    public void setup() throws IOException {

        File f = new File("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        String encodeString = encodeFileToBase64Binary(f);
        System.out.println(encodeString);

        request = new RegisterRequest("FirstName1", "LastName1", encodeString,"@aaa", "Asadsad");



    }

    private static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.getEncoder().encodeToString(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }


    @Test
    public void testRegister_success() throws IOException, URISyntaxException {


        File file = new File("random.jpg");
        String encodeString = encodeFileToBase64Binary(file);
        System.out.println(encodeString);
        request = new RegisterRequest("FirstName1", "LastName1", encodeString,"@aaa", "Asadsad");

    }

}
