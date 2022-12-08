package com.string.operations.stringExamples;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("str")
public class StringController {
    @GetMapping("reverse/{str}")
    public String reverseString(@PathVariable String str){
        StringBuffer sb = new StringBuffer();
        if(str != null) {
            char[] input = str.toCharArray();
            for(int i=input.length -1; i>=0 ; i--){
                sb.append(input[i]);
            }

        }
        return sb.toString();
    }
}
