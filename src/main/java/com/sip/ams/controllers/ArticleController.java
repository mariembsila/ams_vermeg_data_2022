package com.sip.ams.controllers;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import com.sip.ams.repositories.ArticleRepository;
import com.sip.ams.repositories.ProviderRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/article/")
public class ArticleController {
	private final ArticleRepository articleRepository;
	private final ProviderRepository providerRepository;
	
	public static String uploadDirectory =
			System.getProperty("user.dir")+"/src/main/resources/static/uploads";

	@Autowired
	public ArticleController(ArticleRepository articleRepository, ProviderRepository providerRepository) {
		this.articleRepository = articleRepository;
		this.providerRepository = providerRepository;
	}

	@GetMapping("list")
	public String listArticles(Model model) {
		//model.addAttribute("articles", null);
		
		List<Article> la = (List<Article>) articleRepository.findAll();
		if (la.size() == 0)
			la = null;
		
		model.addAttribute("articles", la);
		return "article/listArticles";
	}

	@GetMapping("add")
	public String showAddArticleForm(Article article, Model model) {
		model.addAttribute("providers", providerRepository.findAll());
		model.addAttribute("article", new Article());
		return "article/addArticle";
	}

	@PostMapping("add")
	// @ResponseBody
	public String addArticle(@Valid Article article, BindingResult result,
			@RequestParam(name = "providerId", required = false) Long p,
			@RequestParam("files") MultipartFile[] files) {
		
		Provider provider = providerRepository.findById(p)
				.orElseThrow(() -> new IllegalArgumentException("Invalid provider Id:" + p));
		article.setProvider(provider);
		
		
		/// part upload
		StringBuilder fileName = new StringBuilder();
//		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
//	    Date date = new Date();  
		MultipartFile file = files[0];
		String articleId= String.valueOf(article.getId());
		Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename()+articleId);
		fileName.append(file.getOriginalFilename()+articleId);
		try {
		Files.write(fileNameAndPath, file.getBytes()); //upload
		} catch (IOException e) {
		e.printStackTrace();
		}
		
		article.setPicture(fileName.toString());
		
		articleRepository.save(article);
		return "redirect:list";
		// return article.getLabel() + " " +article.getPrice() + " " + p.toString();
	}

	@GetMapping("delete/{id}")
	public String deleteProvider(@PathVariable("id") long id, Model model) {
		Article article = articleRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid provider Id:" + id));
		try {
			Path fileNameAndPath1 =Paths.get(uploadDirectory,article.getPicture());
			Files.deleteIfExists(fileNameAndPath1);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		articleRepository.delete(article);
		return "redirect:../list";
		//model.addAttribute("articles", articleRepository.findAll());
		//return "/article/listArticles";
	}

	@GetMapping("edit/{id}")
	public String showArticleFormToUpdate(@PathVariable("id") long id, Model model) {
		Article article = articleRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid provider Id:" + id));
		model.addAttribute("article", article);
		model.addAttribute("providers", providerRepository.findAll());
		model.addAttribute("idProvider", article.getProvider().getId());
		return "article/updateArticle";
	}

	@PostMapping("edit")
	public String updateArticle(@Valid Article article, BindingResult result, Model model,
			@RequestParam(name = "providerId", required = false) Long p,
			@RequestParam("files") MultipartFile[] files) throws IOException {
		if (result.hasErrors()) {
			
			return "article/updateArticle";
		}
		Provider provider = providerRepository.findById(p)
				.orElseThrow(() -> new IllegalArgumentException("Invalid provider Id:" + p));
		if (files.length!=0 | files!=null) {
				Path fileNameAndPath1 =Paths.get(uploadDirectory,article.getPicture());
				Files.deleteIfExists(fileNameAndPath1);
			StringBuilder fileName = new StringBuilder();
//			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
//		    Date date = new Date();  
			MultipartFile file = files[0];
//			String articleId= String.valueOf(article.getId());
			Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename()+String.valueOf(article.getId()));
			fileName.append(file.getOriginalFilename()+String.valueOf(article.getId()));
			try {
			Files.write(fileNameAndPath, file.getBytes()); //upload
			} catch (IOException e) {
			e.printStackTrace();
			}
			
			article.setPicture(fileName.toString());
		}
		article.setProvider(provider);
		articleRepository.save(article);
		return "redirect:../list";
		//model.addAttribute("articles", articleRepository.findAll());
		//return "article/listArticles";
	}
	
	@GetMapping("show/{id}")
	public String showArticleDetails(@PathVariable("id") long id, Model model)
	{
	Article article = articleRepository.findById(id)
	.orElseThrow(()->new IllegalArgumentException("Invalid provider Id:" + id));
	model.addAttribute("article", article);
	return "article/showArticle";
	}

}