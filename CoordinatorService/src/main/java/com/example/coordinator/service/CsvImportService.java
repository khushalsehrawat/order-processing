package com.example.coordinator.service;
import com.example.common.enums.OrderStatus;
import com.example.coordinator.dto.ApiDtos.ImportResponse;
import com.example.coordinator.entity.OrderWorkflow;
import com.example.coordinator.repository.OrderWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.apache.commons.csv.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.math.BigDecimal;
import java.time.Instant;
@Service @RequiredArgsConstructor
public class CsvImportService {
 private final OrderWorkflowRepository orders;
 private final OrderProcessingService processing;
 public ImportResponse importFile(MultipartFile file){
   int imported=0,duplicates=0;
   try(BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream()));
       CSVParser parser=CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)){
     for(CSVRecord r:parser){
       String id=get(r,"order_id","external_order_id");
       if(orders.existsByExternalOrderId(id)){duplicates++;continue;}
       OrderWorkflow o=new OrderWorkflow();o.setExternalOrderId(id);
       o.setCustomerId(get(r,"customer_id","customerId"));o.setProductId(get(r,"product_id","productId"));
       o.setQuantity(Integer.valueOf(getOr(r,"quantity","1")));
       o.setAmount(new BigDecimal(getOr(r,"amount","0")));
       o.setFailAt(blankToNull(getOr(r,"fail_at","")));
       o.setCompFailAt(blankToNull(getOr(r,"comp_fail_at","")));
       o.setStatus(OrderStatus.PENDING);o.setCreatedAt(Instant.now());o.setUpdatedAt(Instant.now());
       try{
         o=orders.saveAndFlush(o);
       }catch(DataIntegrityViolationException duplicate){
         duplicates++;
         continue;
       }
       processing.submit(o.getId());imported++;
     }
     return new ImportResponse(imported,duplicates);
   }catch(Exception e){throw new IllegalStateException("CSV import failed: "+e.getMessage(),e);}
 }
 private static String get(CSVRecord r,String a,String b){if(r.isMapped(a))return r.get(a);if(r.isMapped(b))return r.get(b);throw new IllegalArgumentException("Missing column "+a);}
 private static String getOr(CSVRecord r,String a,String d){return r.isMapped(a)&&!r.get(a).isBlank()?r.get(a):d;}
 private static String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}
}
