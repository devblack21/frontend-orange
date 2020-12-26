package com.devblack.frontend.controller;

import com.devblack.frontend.model.Pessoa;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Controller
@RequestMapping
public class PessoaController {

    @Value("${backend.host}")
    private String BACKEND_HOST;

    @Value("${backend.port}")
    private String BACKEND_PORT;

    private String getBackendURL() {
        return "http://" + BACKEND_HOST + ":" + BACKEND_PORT;
    }

    private String formatException(String exception){
        var tagAbertura =  exception.contains("<mensagem>")? "<mensagem>" : "\"mensagem\":";
        var tagFechamento = exception.contains("</mensagem>")? "</mensagem>" : "}";
        return exception
                .substring(
                        exception
                                .indexOf(tagAbertura),
                        exception
                                .indexOf(tagFechamento))
                .replaceAll(tagAbertura,"")
                .replaceAll("\"","");
    }

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("pessoas", getPessoas());
        model.addAttribute("conteudo","fragmentos/tabela");
        return "layout";
    }

    @GetMapping("add")
    public String add(@RequestParam(name = "id",required = false) Long id, ModelMap model) {
        model.addAttribute("pessoa",(id==null)?new Pessoa():getPessoaById(id));
        model.addAttribute("conteudo","fragmentos/add");
        return "layout";
    }

    @PostMapping("save")
    public String save(Pessoa pessoa, Model model) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            if (pessoa.getId() != null && pessoa.getId() > 0){
                restTemplate.put(
                        getBackendURL() + "/orange/pessoa/"+pessoa.getId(), pessoa, Pessoa.class);
            }else
            {
                restTemplate.postForObject(
                        getBackendURL() + "/orange/pessoa", pessoa, Pessoa.class);
            }

            model.addAttribute("success", "Success!");
            model.addAttribute("conteudo","fragmentos/tabela");
            return "layout";

        } catch(Exception e) {
            model.addAttribute("error",formatException(e.getMessage()));
            model.addAttribute("conteudo","fragmentos/add");
            return "layout";
        } finally {
            model.addAttribute("pessoas", getPessoas());
        }
    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable Long id, Model model) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<?> rateResponse =
                    restTemplate.exchange(getBackendURL() + "/orange/pessoa/" + id,
                            HttpMethod.DELETE, null, new ParameterizedTypeReference<Object>() {
                            });

                model.addAttribute("success", "Success!");

        }catch (Exception e){
            model.addAttribute("error",formatException(e.getMessage()));
        }
        model.addAttribute("pessoas", getPessoas());
        model.addAttribute("conteudo","fragmentos/tabela");
        return "layout";
    }

    @SuppressWarnings("unchecked")
    private List<Pessoa> getPessoas() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                getBackendURL() + "/orange/pessoa", List.class);
    }

    @SuppressWarnings("unchecked")
    private Pessoa getPessoaById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(
                getBackendURL() + "/orange/pessoa/"+id, Pessoa.class);
    }
}