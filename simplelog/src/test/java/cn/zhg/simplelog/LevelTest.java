package cn.zhg.simplelog;

import org.junit.Test;

import static org.junit.Assert.*;

public class LevelTest {
    @Test
    public void test(){
        assertTrue(Level.TRACE.match(Level.TRACE));
        assertTrue(Level.TRACE.match(Level.INFO));
        assertTrue(Level.DEBUG.match(Level.INFO));
    }
}