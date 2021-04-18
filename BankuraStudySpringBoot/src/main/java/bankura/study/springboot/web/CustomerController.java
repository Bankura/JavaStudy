package bankura.study.springboot.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bankura.study.springboot.model.SampleBean;

@Controller
@RequestMapping()
public class CustomerController {

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("msg","サンプルメッセージ！");
        return "test/test";
    }

    @RequestMapping(value="/test", method = RequestMethod.POST)
    @ResponseBody
    public String testJson() {
        return "{\"key\":\"value\"}";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String hello(@RequestParam(name = "name", required = false) String name,
                        @RequestParam(name = "value", required = false) String value) {
        return "{\"" + name + "\":\"" + value+ "\"}";
    }

    @RequestMapping(value="/service",consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> top(@RequestBody SampleBean bean) {
        List<String> list = new ArrayList<String>();
        list.add("太宰");
        list.add("夏目");
        list.add(bean.getName());

        return list;
    }
}
