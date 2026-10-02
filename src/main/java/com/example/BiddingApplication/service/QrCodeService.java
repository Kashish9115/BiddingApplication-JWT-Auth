package com.example.BiddingApplication.service;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;
import java.io.*;import java.util.Base64;
@Service
public class QrCodeService {
 public String generateQrBase64(String text)throws Exception{
  BitMatrix m=new MultiFormatWriter().encode(text,BarcodeFormat.QR_CODE,300,300);
  ByteArrayOutputStream out=new ByteArrayOutputStream();MatrixToImageWriter.writeToStream(m,"PNG",out);
  return Base64.getEncoder().encodeToString(out.toByteArray());
 }
}
