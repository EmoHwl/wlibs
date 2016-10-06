package com.wlib.q.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.webkit.URLUtil;

import com.wlib.q.L;
import com.wlib.q.exception.FileCreateFailureException;
import com.wlib.q.file.FileUtils;

/**
 * @author weiliang
 * 2015年10月30日
 * @说明:
 * 位图（bitmap）基础工具
 */
public class BitmapUtils {
	public static boolean saveImage(String path, Bitmap bmp, boolean recycle) {
		return saveImage(path, bmp, 100, recycle);
	}

	public static boolean saveImage(String path, Bitmap bmp, int quality, boolean recycle) {
		File file = FileUtils.getFile(path);
		if(FileUtils.isExistsFile(file)) {
			FileUtils.deleteFileOrDir(file, null, false);
		}
		FileOutputStream out = null;
		try {
			FileUtils.makeFile(file.getPath(), true);
			out = new FileOutputStream(file);
			CompressFormat format = guessImageFormatC(path);
			CompressFormat target;
			if (format == null) {
				target = CompressFormat.JPEG;
			} else if (format == CompressFormat.PNG) {
				target = CompressFormat.PNG;
				quality = 100;
			} else {
				target = format;
			}
			bmp.compress(target, quality, out);
			return true;
		} catch (FileCreateFailureException e) {
			L.e(BitmapUtils.class, e);
		} catch (FileNotFoundException e) {
			L.e(BitmapUtils.class, e);	//吞掉

		} finally {
			FileUtils.closeIO(out);
			if (recycle) bmp.recycle();
		}
		return false;
	}

	public static Bitmap readImage(String path, BitmapFactory.Options opts) {
		return readImage(FileUtils.getFile(path), opts);
	}

	public static Bitmap readImage(File file, BitmapFactory.Options opts) {
		Bitmap bmp = null;
		if(FileUtils.isExistsFile(file)) {
			InputStream in = null;
			try {
				in = new FileInputStream(file);
				bmp = readImage(in, opts);
			} catch (FileNotFoundException e) {
				L.e(BitmapUtils.class, e);	//吞掉，是已经存在的文件了

			} finally {
				FileUtils.closeIO(in);
			}
		}
		return bmp;
	}

	public static Bitmap readImage(Context context, Uri uri, BitmapFactory.Options opts) {
		String scheme = uri.getScheme();
		Bitmap bmp = null;
		if (URLUtil.isNetworkUrl(uri.toString())) {
			bmp = readImageWithUrl(uri.toString(), opts);
		} else if(ContentResolver.SCHEME_FILE.equals(scheme)) {
			bmp = readImage(uri.getPath(), opts);
		} else if(ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			InputStream in = null;
			try {
				in = context.getContentResolver().openInputStream(uri);
				bmp = readImage(in, opts);
			} catch (FileNotFoundException e) {
				L.e(BitmapUtils.class, e);
			} finally {
				FileUtils.closeIO(in);
			}
		} else {
			throw new IllegalArgumentException("不支持的Uri: " + uri.toString());
		}
		return bmp;
	}

	public static Bitmap readImageWithUrl(String url, BitmapFactory.Options opts) {
		Bitmap bmp = null;
		InputStream in = null;
		try {
			in = new URL(url).openStream();
			bmp = readImage(in, opts);
		} catch (MalformedURLException e) {
			L.e(BitmapUtils.class, e);
		} catch (IOException e) {
			L.e(BitmapUtils.class, e);
		} finally {
			FileUtils.closeIO(in);
		}
		return bmp;
	}

	public static Bitmap readImage(InputStream in, BitmapFactory.Options opts) {
		return BitmapFactory.decodeStream(in, null, opts);
	}

	public static Bitmap readImage(Resources res, int drawableId, BitmapFactory.Options opts) {
		return BitmapFactory.decodeResource(res, drawableId, opts);
	}

	public static Bitmap readImage(String path, int width, int height) {
		return readImage(FileUtils.getFile(path), width, height);
	}

	public static Bitmap readImage(File file, int width, int height) {
		Options opts = new Options();
		decodeImageBounds(file, opts);
		return ensureOptsBounds(opts, width, height) ? readImage(file, opts) : readImage(file, null);
	}

	public static Bitmap readImage(Context context, Uri uri, int width, int height) {
		String scheme = uri.getScheme();
		Bitmap bmp = null;
		if (URLUtil.isNetworkUrl(uri.toString())) {
			bmp = readImageWithUrl(uri.toString(), width, height);
		} else if(ContentResolver.SCHEME_FILE.equals(scheme)) {
			bmp = readImage(uri.getPath(), width, height);
		} else if(ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			InputStream in = null;
			try {
				in = context.getContentResolver().openInputStream(uri);
				bmp = readImage(in, width, height);
			} catch (FileNotFoundException e) {
				L.e(BitmapUtils.class, e);
			} finally {
				FileUtils.closeIO(in);
			}
		} else {
			throw new IllegalArgumentException("不支持的Uri: " + uri.toString());
		}
		return bmp;
	}

	public static Bitmap readImageWithUrl(String url, int width, int height) {
		Bitmap bmp = null;
		InputStream in = null;
		try {
			in = new URL(url).openStream();
			bmp = readImage(in, width, height);
		} catch (MalformedURLException e) {
			L.e(BitmapUtils.class, e);
		} catch (IOException e) {
			L.e(BitmapUtils.class, e);
		} finally {
			FileUtils.closeIO(in);
		}
		return bmp;
	}

	public static Bitmap readImage(InputStream in, int width, int height) {
		Options opts = new Options();
		if(!in.markSupported()) in = new BufferedInputStream(in);
		in.mark(Integer.MAX_VALUE);	//无论多大都得缓存，否则无法解析出完整的图片，但是大小肯定不会超限，最多只是相当于把一张图片无压缩的全部读到内存

		//由于BitmapFactory.decodeStream()也会有in.mark()覆盖掉前面的mark()，所以重新new一个

		InputStream in2 = new BufferedInputStream(in);	//in2不能关闭，否则in也会随之关闭

		decodeImageBounds(in2, opts);
		try { in.reset(); } catch (IOException e) { L.e(BitmapUtils.class, e); }
		return ensureOptsBounds(opts, width, height) ? readImage(in, opts) : readImage(in, null);
	}

	public static Bitmap readImage(Resources res, int drawableId, int width, int height) {
		Options opts = new Options();
		decodeImageBounds(res, drawableId, opts);
		if(!ensureOptsBounds(opts, width, height)) {
			opts = null;
		}
		return BitmapFactory.decodeResource(res, drawableId, opts);
	}

	public static int[] decodeImageBounds(String path) {
		return decodeImageBounds(path, null);
	}

	private static int[] decodeImageBounds(String path, Options opt) {
		return decodeImageBounds(FileUtils.getFile(path), opt);
	}

	public static int[] decodeImageBounds(File file) {
		return decodeImageBounds(file, null);
	}

	private static int[] decodeImageBounds(File file, Options opt) {
		if(opt == null) opt = new Options();
		opt.inJustDecodeBounds = true;
		readImage(file, opt);
		L.i(BitmapUtils.class, "opt.mCancel:"+opt.mCancel+", opt.outWidth:"+opt.outWidth+", opt.outHeight:"+opt.outHeight);

		return new int[] {opt.outWidth, opt.outHeight};
	}

	public static int[] decodeImageBounds(Resources res, int drawableId) {
		return decodeImageBounds(res, drawableId, null);
	}

	private static int[] decodeImageBounds(Resources res, int drawableId, Options opt) {
		if(opt == null) opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, drawableId, opt);
		L.i(BitmapUtils.class, "opt.mCancel:"+opt.mCancel+", opt.outWidth:"+opt.outWidth+", opt.outHeight:"+opt.outHeight);

		return new int[] {opt.outWidth, opt.outHeight};
	}

	public static int[] decodeImageBounds(InputStream in) {
		return decodeImageBounds(in, null);
	}

	private static int[] decodeImageBounds(InputStream in, Options opts) {
		if(opts == null) opts = new Options();
		opts.inJustDecodeBounds = true;
		readImage(in, opts);
		L.i(BitmapUtils.class, "opt.mCancel:"+opts.mCancel+", opt.outWidth:"+opts.outWidth+", opt.outHeight:"+opts.outHeight);

		return new int[] {opts.outWidth, opts.outHeight};
	}

	private static boolean ensureOptsBounds(BitmapFactory.Options opts, int width, int height) {
		if(!opts.mCancel && opts.outWidth > 0 && opts.outHeight > 0) {
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = (opts.outWidth*10/width + opts.outHeight*10/height)/20;
			return true;
		}
		return false;
	}

	public static byte[] bmpToBytes(Bitmap bmp, boolean recycle) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.PNG, 100, out);
			return out.toByteArray();
		} finally {
			FileUtils.closeIO(out);
			if (recycle) bmp.recycle();
		}
	}

	public static Bitmap bytesToBmp(byte[] bytes) {
		return readImage(new ByteArrayInputStream(bytes), null);
	}
	
	/** 将图像压缩到指定大小以下。注意：只能生成jpg，因为压缩只对jpg有效。**/
	public static byte[] compressToSize(Bitmap bmp, int bytesLength, boolean recycle) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			int i = 0;
			do {
				
				bmp.compress(CompressFormat.JPEG, 100-i, out);
				if (out.size() >bytesLength) {
					out.reset();
				}else {
					break;
				}
			} while (++i <100);
			return out.toByteArray();
		} finally {
			FileUtils.closeIO(out);
			if (recycle) bmp.recycle();
		}
	}
	
	@SuppressLint("NewApi")
	public static CompressFormat guessImageFormatC(String urlOrPath) {
		CompressFormat format = null;
		String fileName;
		if (URLUtil.isNetworkUrl(urlOrPath)) {
			fileName = URLUtil.guessFileName(urlOrPath, null, null);
		} else if (urlOrPath.lastIndexOf('.') <= 0) {
			return null;
		} else {
			fileName = urlOrPath;
		}
		fileName = fileName.toLowerCase(Locale.US);
		if (fileName.endsWith(".png")) {
			format = CompressFormat.PNG;
		} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			format = CompressFormat.JPEG;
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && fileName.endsWith(".webp")) {
			format = CompressFormat.WEBP;
		}
		return format;
	}

	public static String guessImageFormat(String urlOrPath) {
		CompressFormat format = guessImageFormatC(urlOrPath);
		if (format == null) return null;
		if (format == CompressFormat.JPEG) return "jpg";
		return format.name().toLowerCase(Locale.US);
	}

	//取圆形
	public static Bitmap clipToCircle(Bitmap bmp) {
		float radius = Math.min(bmp.getWidth(), bmp.getHeight()) / 2.0f;
		return clipToCircle(bmp, radius, 0, 0, true);
	}

	//取圆形
	public static Bitmap clipToCircle(Bitmap bmp, float radius, int border, int borderColor, boolean recycle) {
		return clipToOval(bmp, new RectF(0, 0, radius * 2, radius * 2), border, borderColor, recycle);
	}

	//取椭圆形
	public static Bitmap clipToOval(Bitmap bmp) {
		return clipToOval(bmp, new RectF(0, 0, bmp.getWidth(), bmp.getHeight()), 0, 0, true);
	}

	//取椭圆形
	public static Bitmap clipToOval(Bitmap bmp, RectF ovalBounds, int border, int borderColor, boolean recycle) {
		Bitmap destBmp = Bitmap.createBitmap((int)ovalBounds.right, (int)ovalBounds.bottom, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(destBmp);
		Paint paint = new Paint();
		//设置抗锯齿，三者必须同时设置效果才可以

		paint.setAntiAlias(true);	//等同于mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL);
		int saveCount = canvas.save();
		if (border > 0 && borderColor != 0) {
			canvas.clipRect(ovalBounds);
			paint.setColor(borderColor);
			canvas.drawOval(ovalBounds, paint);
			canvas.saveLayer(ovalBounds, paint, Canvas.ALL_SAVE_FLAG);
			ovalBounds.set(ovalBounds.left + border, ovalBounds.top + border, ovalBounds.right - border, ovalBounds.bottom - border);
			canvas.clipRect(ovalBounds);
		}
		paint.setColor(0xff000000);
		canvas.drawOval(ovalBounds, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), ovalBounds, paint);
		paint.setXfermode(null);
		canvas.restoreToCount(saveCount);
		if (recycle) bmp.recycle();
		return destBmp;
	}
	
	/**创建一个虚化效果的Bitmap对象，使用了RenderScript，但是要求最低SDK版本号为17.**/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Bitmap createBlurBmpWithRenderScript(Context context, Bitmap srcBmp, float radius) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) throw new IllegalStateException("要求最低SDK版本号为17");
		if (radius < 1) return null;
		Bitmap bitmap = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(), srcBmp.getConfig());	//srcBmp.copy(srcBmp.getConfig(), true);


		RenderScript rs = RenderScript.create(context);
		Allocation alloc = Allocation.createFromBitmap(rs, srcBmp);
		ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, alloc.getElement());
		blur.setInput(alloc);
		blur.setRadius(radius);
		blur.forEach(alloc);
		alloc.copyTo(bitmap);
		rs.destroy();
		return bitmap;
	}
	
	

	// 缓存
	private Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	// 线程池
	private ExecutorService executorService = Executors.newFixedThreadPool(20);// 总共有10个线程循环使用
	// Hanlder
	private Handler mHandler = new Handler();

	public interface ImageCallback {
		void imageLoad(Bitmap bitmap, String imageUrl);
	}
	

	/**
	 * 先判断本地缓存Cache 有无当前bitmap 如果没有从本地文件获取，如果获取成功，则加入cache，在调用一次自己 失败则从后台加载
	 * @param context
	 * @param imageUrl
	 * @param path
	 * @param imageCallback 如果等于null不会下载图片
	 * @return
	 */
	public Bitmap loadBitmap(Context context,final String imageUrl,final String path,
			final ImageCallback imageCallback) {
		// 如果缓存中有则从缓存中取出来
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
			if (softReference.get() != null) {// 判断是否有drawable
				Bitmap bitmap =  softReference.get(); // 有则返回
				Log.i("loadBitmap", imageUrl+" bitmap = "+bitmap);
				return bitmap;
			}
		}else {
			Bitmap bitmap = readImage(path, new Options()); 
			if (bitmap!=null) {
				imageCache.put(imageUrl, new SoftReference<Bitmap>(bitmap));// 将加载的图片放入到内存中
				Bitmap bitmap2 = loadBitmap(context, imageUrl, path, imageCallback);
				return bitmap2;
			}
		}
		if (imageCallback==null) {
			return null;
		}
		// 使用线程池下载图片
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					if (StringUtils.isEmpty(imageUrl)) {
						return;
					}
					final Bitmap bitmap = getDrawableFormUrl(imageUrl); // 调用获取数据的方法
					imageCache.put(imageUrl, new SoftReference<Bitmap>(
							bitmap));// 将加载的图片放入到内存中
					saveImage(path, bitmap, true);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							imageCallback.imageLoad(bitmap, imageUrl);// 接口回调
						}
					});
				} catch (Exception e) {
					throw new RuntimeException();
				}
			}
		});
		return null;
	}

	/**
	 * 从网络上获取数据
	 */
	public Bitmap getDrawableFormUrl(String imageUrl) {
		Drawable drawable = null;
		Bitmap bitmap = null;
		try {
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), imageUrl);
			bitmap = drawableToBitmap(drawable);
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return bitmap;
	}
	
	/**
	 * 获取裁剪后的圆形图片
	 * 
	 * @param radius
	 *            半径
	 */
	public static Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
		Bitmap scaledSrcBmp;
		int diameter = radius * 2;

		// 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		int squareWidth = 0, squareHeight = 0;
		int x = 0, y = 0;
		Bitmap squareBitmap;
		if (bmpHeight > bmpWidth) {// 高大于宽
			squareWidth = squareHeight = bmpWidth;
			x = 0;
			y = (bmpHeight - bmpWidth) / 2;
			// 截取正方形图片
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else if (bmpHeight < bmpWidth) {// 宽大于高
			squareWidth = squareHeight = bmpHeight;
			x = (bmpWidth - bmpHeight) / 2;
			y = 0;
			squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
					squareHeight);
		} else {
			squareBitmap = bmp;
		}

		if (squareBitmap.getWidth() != diameter
				|| squareBitmap.getHeight() != diameter) {
			scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
					diameter, true);

		} else {
			scaledSrcBmp = squareBitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
				scaledSrcBmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
				scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
				paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
		// bitmap回收(recycle导致在布局文件XML看不到效果)
		// bmp.recycle();
		// squareBitmap.recycle();
		// scaledSrcBmp.recycle();

		// Paint paint2 = new Paint();
		// /* 去锯齿 */
		// paint2.setAntiAlias(true);
		// paint2.setFilterBitmap(true);
		// paint2.setDither(true);
		// paint2.setColor(0xffc3c3c3);
		// /* 设置paint的　style　为STROKE：空心 */
		// paint2.setStyle(Paint.Style.STROKE);
		// /* 设置paint的外框宽度 */
		//
		// float scale = context.getResources().getDisplayMetrics().density;
		// Log.i("dhp","scale="+scale);
		// int sw = (int)(5*scale);
		// paint2.setStrokeWidth(sw);
		// canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
		// scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth()/2, paint2);

		bmp = null;
		squareBitmap = null;
		scaledSrcBmp = null;
		return output;
	}

	/**
	 * drawable 转化 bitmap
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * bitmap to drawable
	 * @param res
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Resources res ,Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(res, bitmap);
		return drawable;
	}
}