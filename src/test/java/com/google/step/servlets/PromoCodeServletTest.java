package com.google.step.servlets;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.anyString;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PromoCodeServletTest {
        
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_ID_THAT_DOES_NOT_EXIST = "CHANNEL_ID_THAT_DOES_NOT_EXIST";
    private static final String CHANNEL_ID_THAT_EXISTS = "CHANNEL_ID_THAT_EXISTS";

    @Mock
    HttpServletRequest request;
 
    @Mock
    HttpServletResponse response;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void properChannelIdRequest() throws ServletException, IOException {
        when(request.getParameter("formInput")).thenReturn(CHANNEL_ID);
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(response.getWriter()).thenReturn(pw);

        PromoCodeServlet promoCodeServlet = new PromoCodeServlet();
        //promoCodeServlet.doGet(request, response);
        //String result = sw.getBuffer().toString();

        assertThat(1 == 0 , equalTo(false));

    }
}
