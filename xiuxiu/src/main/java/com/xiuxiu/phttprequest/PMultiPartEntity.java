package com.xiuxiu.phttprequest;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class PMultiPartEntity extends MultipartEntity {

	private PostProgressListener mPostProgressListener;
	
	public PMultiPartEntity(PostProgressListener postProgressListener) {
		super();
		// TODO Auto-generated constructor stub
		this.mPostProgressListener = postProgressListener;
	}

	public PMultiPartEntity(HttpMultipartMode mode, String boundary,
                            Charset charset, PostProgressListener postProgressListener) {
		super(mode, boundary, charset);
		// TODO Auto-generated constructor stub
		this.mPostProgressListener = postProgressListener;
	}

	public PMultiPartEntity(HttpMultipartMode mode, PostProgressListener postProgressListener) {
		super(mode);
		// TODO Auto-generated constructor stub
		this.mPostProgressListener = postProgressListener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		// TODO Auto-generated method stub
		super.writeTo(new CountingOutputStream(outstream, mPostProgressListener));
	}

	////////////////////////////////////
	public interface PostProgressListener{
		void transferred(long num);
	}
	
	public class CountingOutputStream extends FilterOutputStream {

		private PostProgressListener mListener;
		private long mTransferred;
		
		public CountingOutputStream(OutputStream out, PostProgressListener listener) {
			super(out);
			// TODO Auto-generated constructor stub
			this.mListener = listener;
			this.mTransferred = 0;
		}

		@Override
		public void write(byte[] buffer, int offset, int length)
				throws IOException {
			// TODO Auto-generated method stub
			out.write(buffer, offset, length);
			this.mTransferred += length;
			this.mListener.transferred(this.mTransferred);
		}

		@Override
		public void write(int oneByte) throws IOException {
			// TODO Auto-generated method stub
			out.write(oneByte);
			this.mTransferred++;
			this.mListener.transferred(this.mTransferred);
		}
		
	}
	
}
