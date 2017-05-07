package com.github.coolcool.sloth.lianjiadb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.coolcool.sloth.lianjiadb.common.MailUtil;
import com.github.coolcool.sloth.lianjiadb.model.*;
import com.github.coolcool.sloth.lianjiadb.model.Process;
import com.github.coolcool.sloth.lianjiadb.service.*;
import com.github.coolcool.sloth.lianjiadb.service.impl.support.LianjiaWebUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import com.github.coolcool.sloth.lianjiadb.mapper.ProcessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.github.coolcool.sloth.lianjiadb.common.Page;
import javax.annotation.Generated;


@Generated(
	value = {
		"https://github.com/coolcooldee/sloth",
		"Sloth version:1.0"
	},
	comments = "This class is generated by Sloth"
)
@Service
public  class ProcessServiceImpl implements ProcessService{

	Logger logger = LoggerFactory.getLogger(ProcessService.class);


	@Autowired
	private AreaService areaService;

	@Autowired
	private HouseService houseService;

	@Autowired
	private HouseindexService houseindexService;

	@Autowired
	private HousepriceService housepriceService;

	@Autowired
	private ProcessMapper processMapper;

	@Value("${com.github.coolcool.sloth.lianjiadb.timetask.GenAndExeDailyProcessTimeTask.notifyAreas:}")
	String notifyAreas;


	@Override
	public void fetchHouseUrls() throws InterruptedException {
		List<Process> processes = processMapper.listUnFinished();
		for (int i = 0; i < processes.size(); i++) {
			Process process = processes.get(i);
			if(process.getFinished()>0){
				continue;
			}
			//在售房源抓取
			if(process.getType()==1){
				int totalPageNo = LianjiaWebUtil.fetchAreaTotalPageNo(process.getArea());
				Thread.sleep(500);
				logger.info(process.getArea()+" total pageno is "+totalPageNo);
				if(totalPageNo==0){
					process.setPageNo(0);
					process.setFinished(1);
					process.setFinishtime(new Date());
					this.update(process);
					continue;
				}

				while(process.getPageNo()<=totalPageNo && process.getFinished()==0){
					Set<String> urls = LianjiaWebUtil.fetchAreaHouseUrls(process.getArea(), process.getPageNo());
					Thread.sleep(500);
					Iterator<String> iurl = urls.iterator();
					while (iurl.hasNext()){
						String houseUrl = iurl.next();
						Houseindex houseindex = new Houseindex(houseUrl);
						Houseindex tempHouseIndex = houseindexService.getByCode(houseindex.getCode());
						if(tempHouseIndex!=null){
							if(tempHouseIndex.getStatus()==1)
								continue;
							else{
								houseindex.setStatus(1);//设置为在售
								houseindex.setUpdatetime(new Date());
								houseindexService.update(houseindex);
								logger.info("changed selling house index :"+JSONObject.toJSONString(houseindex));
							}
						}else {
							//insert to db
							houseindex.setStatus(1);//设置为在售
							houseindex.setUpdatetime(new Date());
							houseindexService.save(houseindex);
							logger.info("saved selling house index : "+JSONObject.toJSONString(houseindex));
							//是否需要通知
							if( !StringUtils.isEmpty(notifyAreas) && notifyAreas.indexOf(","+process.getArea()+",")>-1){
								House nowhouse = LianjiaWebUtil.fetchAndGenHouseObject(houseindex.getUrl());
								Thread.sleep(500);
								//邮件通知价格变动
								String subject = "【新房源上线通知】".concat(nowhouse.getAreaName()).concat(houseindex.getCode());
								String content = "<br/>" +
										nowhouse.getTitle()+"<br/>" +
										nowhouse.getSubtitle()+"<br/>" +
										"【地址】："+nowhouse.getAreaName()+"<br/>" +
										"【价格】："+nowhouse.getPrice()+"万 <br/>" +
										"【均价】："+nowhouse.getUnitprice()+"万 <br/>" +
										"【面积】："+nowhouse.getAreaMainInfo() +"<br/>" +
										"【楼龄】："+nowhouse.getAreaSubInfo() +"<br/>" +
										"【室厅】："+nowhouse.getRoomMainInfo() +"<br/>" +
										"【楼层】："+nowhouse.getRoomSubInfo() +"<br/>" +
										"【朝向】："+nowhouse.getRoomMainType() +"<br/>" +
										"【装修】："+nowhouse.getRoomSubType()+"<br/>" +
										"【源地址】：<a href=\""+houseindex.getUrl()+"\">"+houseindex.getUrl()+"</a>"+
										"";
								MailUtil.send(subject, content);
							}

						}
					}

					if(process.getPageNo()==totalPageNo){
						process.setFinished(1);
						process.setFinishtime(new Date());
					}else{
						process.setPageNo(process.getPageNo()+1);
					}
					//insert to db
					this.update(process);
					process.setPageNo(process.getPageNo()+1);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			//已经成交房源抓取
			else if(process.getType()==2){
				int totalPageNo = LianjiaWebUtil.fetchAreaChenjiaoTotalPageNo(process.getArea());
				Thread.sleep(500);
				logger.info(process.getArea()+" chengjiao total pageno is "+totalPageNo);
				if(totalPageNo==0){
					process.setPageNo(0);
					process.setFinished(1);
					process.setFinishtime(new Date());
					this.update(process);
					continue;
				}

				while(process.getPageNo()<=totalPageNo && process.getFinished()==0){
					Set<String> urls = LianjiaWebUtil.fetchAreaChenjiaoHouseUrls(process.getArea(), process.getPageNo());
					Thread.sleep(500);

					Iterator<String> iurl = urls.iterator();
					while (iurl.hasNext()){
						String houseUrl = iurl.next();
						Houseindex houseindex = new Houseindex(houseUrl);
						Houseindex tempHouseIndex = houseindexService.getByCode(houseindex.getCode());
						if(tempHouseIndex!=null){
							if(tempHouseIndex.getStatus()==2)
								continue;
							else{
								tempHouseIndex.setUpdatetime(new Date());
								tempHouseIndex.setStatus(2);//设置为已成交
								houseindexService.update(tempHouseIndex);
								logger.info("changed sold houseindex :"+JSONObject.toJSONString(houseindex));
							}
						}else {
							//insert to db
							houseindex.setCreatetime(new Date());
							houseindex.setUpdatetime(new Date());
							houseindex.setStatus(2);//设置为已成交
							houseindexService.save(houseindex);
							logger.info("saved sold houseindex : "+JSONObject.toJSONString(houseindex));
						}
					}

					if(process.getPageNo()==totalPageNo){
						process.setFinished(1);
						process.setFinishtime(new Date());
					}else{
						process.setPageNo(process.getPageNo()+1);
					}
					//insert to db
					this.update(process);
					process.setPageNo(process.getPageNo()+1);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	@Override
	public void fetchHouseDetail() throws InterruptedException {
		int pageNo = 1;
		int pageSize = 300;
		boolean stop = false;
		while (!stop) {
			//分页获取 hasDetail 状态为 0  的 houseindex
			List<Houseindex> houseindexList = houseindexService.listTodayHasNotDetail(pageNo, pageSize);
			if(houseindexList==null ||  houseindexList.size()==0)
				break;
			//fetch detail
			for (int i = 0; i < houseindexList.size(); i++) {
				Houseindex h = houseindexList.get(i);
				House house = LianjiaWebUtil.fetchAndGenHouseObject(h.getUrl());
				Thread.sleep(500);
				if(StringUtils.isEmpty(house.getTitle())|| StringUtils.isBlank(house.getTitle())){
					logger .info("house title is null "+JSONObject.toJSONString(house));
					h.setStatus(-2);
					h.setUpdatetime(new Date());
					h.setHasdetail(1);
					houseindexService.update(h);
				}else{

					if(h.getStatus()==1){
						//insert into db
						houseService.save(house);
						h.setStatus(1);
						h.setUpdatetime(new Date());
						h.setHasdetail(1);
						houseindexService.update(h);
						logger.info("saving selling house:"+ JSONObject.toJSONString(house));
					}else{
						//insert into db
						houseService.save(house);
						h.setStatus(2);
						h.setUpdatetime(new Date());
						h.setHasdetail(1);
						houseindexService.update(h);
						logger.info("saving sold house:"+ JSONObject.toJSONString(house));
					}
				}
			}
		}

	}

	@Override
	public void checkChange() throws InterruptedException {
		//遍历house，检查价格变化、下架
		int start = 0 ;
		int step = 300;

		while (true) {
			//分页获取今天需要被检测的houseIndex
			List<Houseindex> houseindexList = houseindexService.listTodayUnCheck(start, step);

			logger.info("checking price ..."+houseindexList.size());

			if(houseindexList==null || houseindexList.size()==0)
				break;

			for (int i = 0; i < houseindexList.size(); i++) {

				try {
					Thread.sleep(1000);
				}catch (Throwable t){
					t.printStackTrace();
				}

				Houseindex houseindex = houseindexList.get(i);
				String houseHtml = LianjiaWebUtil.fetchHouseHtml(houseindex.getUrl());
				Thread.sleep(500);
				if("error".equals(houseHtml)){
					//商品页面找不到，永久重定向
					logger.info("house is not found, "+JSONObject.toJSONString(houseindex));
					houseindex.setStatus(-301); //找不到
					houseindexService.update(houseindex);
					houseindex.setLastCheckDate(new Date());
					continue;
				}

				//判断是否下架
				boolean remove = LianjiaWebUtil.getRemoved(houseHtml);
				if(remove){
					logger.info("house is removed, "+JSONObject.toJSONString(houseindex));
					houseindex.setStatus(-1); //已下架
					houseindex.setLastCheckDate(new Date());
					houseindexService.update(houseindex);
					continue;
				}

				//判断是否成交


				//判断价格变更
				BigDecimal nowprice = LianjiaWebUtil.getPrice(houseHtml);
				House nowhouse = LianjiaWebUtil.getAndGenHouseObject(houseindex.getUrl(), houseHtml);
				if(nowprice==null){
					logger.info("nowprice is null, "+ JSONObject.toJSONString(houseindex));
					continue;
				}

				Houseprice previousHouseprice = housepriceService.getPrevious(houseindex.getCode());
				if(previousHouseprice==null){
					//save newest price
					Houseprice tempHousePrice = new Houseprice(houseindex.getCode(), nowprice.doubleValue());
					housepriceService.save(tempHousePrice);
					logger.info("saving newest price :"+ JSONObject.toJSONString(tempHousePrice));
				}else if(previousHouseprice.getPrice()!=nowprice.doubleValue()){
					//save price change
					boolean up = true;
					String  temp = "0";
					temp = Math.abs(previousHouseprice.getPrice() - nowprice.doubleValue())+"";
					if(previousHouseprice.getPrice()>nowprice.doubleValue()){
						up = false;

					}
					Houseprice tempHousePrice = new Houseprice(houseindex.getCode(), nowprice.doubleValue());
					housepriceService.save(tempHousePrice);
					logger.info("changing newest price "+(up?"up:":"down:")+JSONObject.toJSONString(previousHouseprice)+"，"+JSONObject.toJSONString(tempHousePrice));

					//邮件通知价格变动
					String subject = "【房源价格调整】".concat("价格").concat((up?"上升:":"下降:")).concat(temp).concat("万").concat(houseindex.getCode());
					String content = "<br/>" +
							nowhouse.getTitle()+"<br/>" +
							nowhouse.getSubtitle()+"<br/>" +
							"【地址】："+nowhouse.getAreaName()+"<br/>" +
							"【价格】："+nowhouse.getPrice()+"万 <br/>" +
							"【均价】："+nowhouse.getUnitprice()+"万 <br/>" +
							"【面积】："+nowhouse.getAreaMainInfo() +"<br/>" +
							"【楼龄】："+nowhouse.getAreaSubInfo() +"<br/>" +
							"【室厅】："+nowhouse.getRoomMainInfo() +"<br/>" +
							"【楼层】："+nowhouse.getRoomSubInfo() +"<br/>" +
							"【朝向】："+nowhouse.getRoomMainType() +"<br/>" +
							"【装修】："+nowhouse.getRoomSubType()+"<br/>" +
							"【源地址】：<a href=\""+houseindex.getUrl()+"\">"+houseindex.getUrl()+"</a>"+
							"";
					MailUtil.send(subject, content);
				}else{
					logger.info("price is the same,"+JSONObject.toJSONString(previousHouseprice));
				}
				houseindexService.setTodayChecked(houseindex.getCode());
			}

		}


	}

	public Integer save(Process process){
		return processMapper.insert(process);
	}

	@Override
	public Process getById(Object id){
		return processMapper.getByPrimaryKey(id);
	}
	@Override
	public void deleteById(Object id){
		processMapper.deleteByPrimaryKey(id);
	}
	@Override
	public void update(Process process){
		processMapper.updateByPrimaryKey(process);
	}

	@Override
	public Integer count(){
	    return processMapper.count();
	}

	@Override
	public List<Process> list(){
		return processMapper.list();
	}

	@Override
	public Page<Process> page(int pageNo, int pageSize) {
		Page<Process> page = new Page<>();
        int start = (pageNo-1)*pageSize;
        page.setPageSize(pageSize);
        page.setStart(start);
        page.setResult(processMapper.page(start,pageSize));
        page.setTotalCount(processMapper.count());
        return page;
	}

	@Override
	public Integer increment(){
		return processMapper.increment();
	}

	@Override
	public void genProcesses() {

		int cityId = 1; //广州

		List<Area> childenAreas = areaService.listTwoLevelChilden(cityId);
		for (int i = 0; i < childenAreas.size(); i++) {
			Area area = childenAreas.get(i);
			//判断今天是否已经存在计划任务
			int count = countTodayProcessByAreaCode(area.getCode());
			System.out.println(count);
			if(count>0)
				continue;
			Process process = new Process();
			process.setPageNo(0);
			process.setArea(area.getCode());
			process.setType(1);
			process.setFinished(0);
			System.out.println("---pre save1---");

			save(process);
			System.out.println("---save1---");
			Process process2 = new Process();
			process2.setPageNo(0);
			process2.setArea(area.getCode());
			process2.setType(2);
			process2.setFinished(0);
			System.out.println("---pre save2---");

			save(process2);
			System.out.println("---save1---");

			logger.info("add  process "+ JSONObject.toJSONString(process));
			logger.info("add  process2 "+ JSONObject.toJSONString(process2));
		}
	}

	@Override
	public int countTodayProcessByAreaCode(String areaCode) {
		return processMapper.countTodayProcessByAreaCode(areaCode);
	}


	public static void main(String[] args) {
		double a = 134.0;
		double b = 135.0;
		System.out.println(Math.abs((b-a)));
	}

}