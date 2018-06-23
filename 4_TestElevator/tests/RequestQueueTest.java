package elev;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.ByteArrayInputStream;
public class RequestQueueTest {

    @Test
    public void testRequestQueue() {
        RequestQueue rq = new RequestQueue();
        assertTrue(rq.repOk());
    }

    @Test
    public void testParseInput() {
        ByteArrayInputStream in = new ByteArrayInputStream((
                "(FR,2,UP,1)\r\n" + 
                "(FR,1,UP,0)\r\n" + 
                "(FR,12,UP,0)\r\n" + 
                "(ER,4,0)\r\n" + 
                "(ER,4,1)\r\n" + 
                "(FR,2,UP,1)\r\n" + 
                "(FR,2,UP,2)\r\n" + 
                "(ER,9,4)\r\n" + 
                "(ER,7,3)\r\n" + 
                "(ER,9,4)\r\n" + 
                "(FR,3,DOWN,5)\r\n" + 
                "RUN").getBytes());
        System.setIn(in);
        RequestQueue rq = new RequestQueue();
        rq.ParseInput();
        assertTrue(rq.repOk()&&rq.rq.size()==8);
    }

}
