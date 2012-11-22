package com.next.infotech.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gdata.data.photos.PhotoEntry;
import com.next.core.exception.AppException;
import com.next.core.exception.InternalAppException;
import com.next.core.picasa.PicasaUploadUtil;
import com.next.infotech.cache.UserLocationCache;
import com.next.infotech.concurrent.CounterNames;
import com.next.infotech.persistance.domain.UserCacheDomain;
import com.next.infotech.persistance.domain.UserDomain;
import com.next.infotech.persistance.domain.UserPublicDomain.Gender;
import com.next.infotech.persistance.services.ChatAroundServices;

@Controller
public class ImageController extends BaseController{

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ChatAroundServices chatAroundServices;
	@Autowired
	private UserLocationCache userLocationCache;

	private static String userName = "chataround2012@gmail.com";
	private static String password = "batchu2012";
	private static PicasaUploadUtil picasaUploadUtil = new PicasaUploadUtil();
	static{
		picasaUploadUtil.setClientName("ChatAroundMobileApp");
	}
	@RequestMapping(value="/api/1.0/uploadimage", method = RequestMethod.POST)
    @ResponseBody
	public String uploadImage(HttpServletRequest httpServletRequest) throws AppException{
		counterManager.incrementCounter(CounterNames.TOTAL_UPLOAD_IMAGE_REQUEST);
		try {
			ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
			//FileItemIterator iterator = upload.getItemIterator(httpServletRequest);
			List items = upload.parseRequest(httpServletRequest);
			Iterator itr = items.iterator();

			InputStream imageInputStream = null;
			String userExternalId = null;
			while(itr.hasNext()) {
				FileItem item = (FileItem) itr.next();
				logger.info("FileItem item.isFormField()="+item.isFormField()+" , item.getFieldName()=" +item.getFieldName());
				if (item.isFormField()) {
					if ("USERID".equalsIgnoreCase(item.getFieldName()))
						logger.info("         item.toString()="+item.getString());
						userExternalId = item.getString();
				} else {
					imageInputStream = item.getInputStream();
				}
			}
			
			boolean isMultipart = ServletFileUpload.isMultipartContent(httpServletRequest);
			logger.info("isMultipart="+isMultipart);
			/*
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();
				logger.info("item.isFormField()="+item.isFormField()+" , item.getFieldName()=" +item.getFieldName());
				logger.info("         item.toString()="+item.toString());
				if (item.isFormField()) {
					if ("UserId".equals(item.getFieldName()))
						userExternalId = item.toString();
				} else {
					imageInputStream = stream;
				}
				
			}
			*/
			if(imageInputStream == null){
				throw new AppException("No Image found in request");
			}
			if(userExternalId == null){
				throw new AppException("No User external id found in request");
			}
			logger.info("Searching user by External ID="+userExternalId);
			UserDomain user = chatAroundServices.getUserByExternalId(userExternalId);
			if(user == null){
				throw new AppException("No User exists with id="+userExternalId);
			}
			String title = user.getUserId();
			String description = user.getUserId() + ","+user.getNickName();
			logger.info("title="+title);
			logger.info("description="+description);
			
			PhotoEntry photo = picasaUploadUtil.uploadPhoto(userName,password, "5812815249223925377", title,description, imageInputStream);

			String smallPhotoUrl = null;
			String mediumImageUrl = null;
			String largeImageUrl = null;
			try{
				smallPhotoUrl = photo.getMediaThumbnails().get(0).getUrl();
				mediumImageUrl = photo.getMediaThumbnails().get(1).getUrl();
				largeImageUrl = photo.getMediaThumbnails().get(2).getUrl();
			}catch(Exception ex){
				
			}
			logger.info("smallPhotoUrl="+smallPhotoUrl);
			logger.info("mediumImageUrl="+mediumImageUrl);
			logger.info("largeImageUrl="+largeImageUrl);
			
			logger.info("UserBeforeUpdate="+user);
			UserDomain updatedUser = chatAroundServices.updateUserPhotoUrls(userExternalId, smallPhotoUrl, mediumImageUrl, largeImageUrl);
			logger.info("updatedUser="+updatedUser);
			counterManager.incrementCounter(CounterNames.TOTAL_SUCCESS_UPLOAD_IMAGE_REQUEST);
		} catch (FileUploadException e) {
			counterManager.incrementCounter(CounterNames.TOTAL_FAILED_UPLOAD_IMAGE_REQUEST);
			throw new InternalAppException(e);
		} catch (IOException e) {
			counterManager.incrementCounter(CounterNames.TOTAL_FAILED_UPLOAD_IMAGE_REQUEST);
			throw new InternalAppException(e);
		}		
		
		return "Success";
	}
	
	@RequestMapping(value="/api/1.0/testuploadimage", method = RequestMethod.GET)
	public String testUploadImage(HttpServletRequest httpServletRequest) throws AppException{
		return "testupload";
	}
	@RequestMapping(value="/api/1.0/userthumbnail", method = RequestMethod.GET)
	public ModelAndView getUserThumbNail(HttpServletRequest httpServletRequest,ModelAndView mv,@RequestParam("userId") String userId) throws AppException{
		counterManager.incrementCounter(CounterNames.TOTAL_USER_THUMBNAIL_REQUEST);
		logger.info("getting user by userId from cache="+userId);
		UserCacheDomain user = userLocationCache.getUserByExternalId(userId);
		if(user == null){
			logger.info("getting user by userId from DB="+userId);
			//search it in DB
			user = chatAroundServices.getUserByExternalId(userId);
		}
		logger.info("User="+user);
		String redirectUrl = user.getSmallImageUrl();
		String imageSize = httpServletRequest.getParameter("size");
		if(redirectUrl != null && imageSize != null && !imageSize.trim().equals("")){
			String imageSizeUpper = imageSize.toUpperCase();
			int size = 0;
			try{
				size = Integer.parseInt(imageSize);
			}catch(Exception ex){
				
			}
			if(size > 0){
				//REPLACE S72 IN THE SMALL URL
				if (redirectUrl.indexOf("s72") > 0){
					redirectUrl = redirectUrl.replaceAll("s72", "s"+size);
				}
			}else{
				if("SMALL".equals(imageSizeUpper)){
					redirectUrl = user.getSmallImageUrl();
				}
				if("MEDIUM".equals(imageSizeUpper)){
					redirectUrl = user.getMediumImageUrl();
				}
				if("LARGE".equals(imageSizeUpper)){
					redirectUrl = user.getLargeImageUrl();
				}
			}
		}
		if(redirectUrl == null){
			if(Gender.Male.equals(user.getGender())){
				redirectUrl="http://cdn1.iconfinder.com/data/icons/general10/png/128/administrator.png";	
			}
			if(Gender.Female.equals(user.getGender())){
				redirectUrl="http://cdn1.iconfinder.com/data/icons/CrystalClear/128x128/kdm/user_female.png";	
			}
			if(Gender.Other.equals(user.getGender())){
				redirectUrl="http://cdn1.iconfinder.com/data/icons/sphericalcons/128/users%201.png";	
			}
			
		}
		logger.info("Rediecting to url="+redirectUrl);
		RedirectView view = new RedirectView(redirectUrl);
		mv.setView(view);
		return mv;
	}
	
	public static void main(String[] args) throws AppException, FileNotFoundException{
		picasaUploadUtil.displayAllAlbums(userName, password);
		System.out.println("**Done***" );
		//114941734671985059551
		//5812815249223925377
		InputStream is = new FileInputStream(new File("C:\\Users\\Ravi\\Downloads\\Dueamountislessthantotalbil.png"));
		picasaUploadUtil.uploadPhoto(userName, password, "5812815249223925377", "Dueamountislessthantotalbil", "Dueamountislessthantotalbil", is);
	}
	
}
