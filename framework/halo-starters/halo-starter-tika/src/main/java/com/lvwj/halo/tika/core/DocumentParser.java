package com.lvwj.halo.tika.core;


import java.io.InputStream;

/**
 * @author lvweijie
 * @date 2024年07月18日 14:41
 */
public interface DocumentParser {

    String parse(InputStream inputStream);
}
