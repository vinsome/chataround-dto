package com.service.chataroundpush.service.impl;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.android.gcm.demo.server.SendMessageServlet;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.service.chataround.dto.chat.ChatAroundDto;
import com.service.chataround.util.PushUtils;
import com.service.chataroundpush.server.PushDatastore;
import com.service.chataroundpush.service.PushService;

@Service("pushService")
public class PushServiceImpl implements PushService {
	protected final Logger logger = Logger.getLogger(getClass().getName());

	public ChatAroundDto pushMessage(ChatAroundDto dto) {
		logger.info("PushServiceImpl pushMessage() Begin");
		return doPush(dto);
	}

	private  ChatAroundDto doPush (ChatAroundDto dto) {
		String appId = dto.getAppId();
		String regId = dto.getDeviceId();// is who sends the message!
		//Map<String, String> params = dto.getParams();
		logger.info("PushServiceImpl doPush  deviceId "+regId);
		Map<String,String> devices = PushDatastore.getDevicesToMessage(appId);
		logger.info("PushServiceImpl doPush devices selected "+devices);
	    String status;
	    if (devices.isEmpty()) {
	      status = "Message ignored as there is no device registered!";
	    } else {
	      Queue queue = QueueFactory.getQueue("gcm");
	      // NOTE: check below is for demonstration purposes; a real application
	      // could always send a multicast, even for just one recipient
	      if (devices.size() == 1) {
	        // send a single message using plain post
	        String device = devices.get(devices.keySet().iterator().next());
	        
	        queue.add(withUrl("/send")
	        		.param(SendMessageServlet.PARAMETER_DEVICE, device)
	        		.param(PushUtils.PARAMETER_MESSAGE, dto.getMessage())
	        		.param(PushUtils.NICK_ID_FROM_MESSANGER, dto.getNickName())
	        		.param(PushUtils.REG_ID_FROM_MESSANGER,regId)
	        );
	        status = "Single message queued for registration id " + device;
	      } else {
	        // send a multicast message using JSON
	        // must split in chunks of 1000 devices (GCM limit)
	        int total = devices.size();
	        List<String> partialDevices = new ArrayList<String>(total);
	        int counter = 0;
	        int tasks = 0;
	        Iterator<String> devicesInt = devices.keySet().iterator();
	        	while(devicesInt.hasNext()){
	        		String device = devices.get(devicesInt.next());
	  	          counter++;
		          partialDevices.add(device);
		          int partialSize = partialDevices.size();
		          if (partialSize == PushDatastore.MULTICAST_SIZE || counter == total) {
		            String multicastKey = PushDatastore.createMulticast(partialDevices);
		            logger.fine("Queuing " + partialSize + " devices on multicast " +
		                multicastKey);
		            TaskOptions taskOptions = TaskOptions.Builder
		                .withUrl("/send")
		                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
		                .param(PushUtils.PARAMETER_MESSAGE, dto.getMessage())
		        		.param(PushUtils.NICK_ID_FROM_MESSANGER, dto.getNickName())
		                .param(PushUtils.REG_ID_FROM_MESSANGER,regId)
		                .method(Method.POST);
		            queue.add(taskOptions);
		            partialDevices.clear();
		            tasks++;
		          }	        		
	        	}
	        	/*
	        for (String device : devices) {
	          counter++;
	          partialDevices.add(device);
	          int partialSize = partialDevices.size();
	          if (partialSize == PushDatastore.MULTICAST_SIZE || counter == total) {
	            String multicastKey = PushDatastore.createMulticast(partialDevices);
	            logger.fine("Queuing " + partialSize + " devices on multicast " +
	                multicastKey);
	            TaskOptions taskOptions = TaskOptions.Builder
	                .withUrl("/send")
	                .param(SendMessageServlet.PARAMETER_MULTICAST, multicastKey)
	                .param(PushUtils.PARAMETER_MESSAGE, dto.getMessage())
	        		.param(PushUtils.NICK_ID_FROM_MESSANGER, dto.getNick())
	        		.param(PushUtils.LANGUAGE_ID_FROM_MESSANGER, dto.getParams().get("languageId"))	                
	                .param(PushUtils.REG_ID_FROM_MESSANGER,regId)
	                .method(Method.POST);
	            queue.add(taskOptions);
	            partialDevices.clear();
	            tasks++;
	          }
	        }
	        */
	        status = "Queued tasks to send " + tasks + " multicast messages to " +
	            total + " devices";
	      }
	    }
	    logger.info("PushServiceImpl doPush status=[" +status+"]");
		return dto;
	}

}
