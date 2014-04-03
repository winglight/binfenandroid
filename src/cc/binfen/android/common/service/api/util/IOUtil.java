package cc.binfen.android.common.service.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	public static void write(InputStream input,OutputStream output) throws IOException{
		byte[] bs = new byte[1024];
		while (input.read(bs) != -1) {
			output.write(bs);
		}
		output.flush();
		output.close();
		input.close();
	}
	public static void copy(InputStream input,OutputStream output) throws IOException{
		write(input, output);
	}
}
