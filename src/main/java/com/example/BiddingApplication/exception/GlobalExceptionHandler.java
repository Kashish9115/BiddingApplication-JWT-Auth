package com.example.BiddingApplication.exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice @Slf4j
public class GlobalExceptionHandler {
 @ExceptionHandler(BadRequestException.class)
 public String bad(BadRequestException e,HttpServletRequest r,Model m){
  log.warn("Bad request path={} message={}",r.getRequestURI(),e.getMessage());
  m.addAttribute("error",e.getMessage()); return "error";
 }
 @ExceptionHandler(Exception.class)
 public String any(Exception e,HttpServletRequest r,Model m){
  log.error("Unhandled exception path={}",r.getRequestURI(),e);
  m.addAttribute("error","Something went wrong. Please try again."); return "error";
 }
}
