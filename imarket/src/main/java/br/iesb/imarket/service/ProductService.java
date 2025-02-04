package br.iesb.imarket.service;

import br.iesb.imarket.dto.request.ProductDTO;
import br.iesb.imarket.exception.ProductNotFoundException;
import br.iesb.imarket.model.Product;
import br.iesb.imarket.repository.ProdRepo;
import br.iesb.imarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProdRepo repository;

    @Autowired
    private ProductRepository productRepo;

    public List<ProductDTO> getProduct(){
        List<ProductDTO> listProducts = new ArrayList<>();
        Iterator<Product> resultBank = repository.findAll().iterator();

        while(resultBank.hasNext()){
            Product product = resultBank.next();
            ProductDTO productDTO = new ProductDTO(product.getId(), product.getName(), product.getBrand(), product.getPriceDto(), product.getQuantity(), product.getDescription(), product.isPromotion(), product.getPercent(), product.getCategory());
            listProducts.add(productDTO);
        }
        return listProducts;
    }

    public List<ProductDTO> getProductCategory(String category){
        List<ProductDTO> listProducts = new ArrayList<>();
        Optional<List<Product>> resultBank = repository.findByCategoryContaining(category);
        entityForDto(listProducts,resultBank);

        return listProducts;
    }

    public List<ProductDTO> getProductBrand(String brand){
        List<ProductDTO> listProducts = new ArrayList<>();
        Optional<List<Product>> resultBank = repository.findByBrandContaining(brand);
        entityForDto(listProducts, resultBank);

        return listProducts;
    }

    public List<ProductDTO> getProductsPromotion(){
        List<ProductDTO> listProducts = new ArrayList<>();
        Optional<List<Product>> resultBank = repository.getByPromotionIsTrue();
        entityForDto(listProducts, resultBank);

        return listProducts;
    }

    public List<ProductDTO> getProductsCrescente(){
        List<ProductDTO> listProducts = new ArrayList<>();

        List<Product> aux = productRepo.getProductsCrescente();
        //entity_dto(listProducts, aux);
        return listProducts;
    }

    public int saveProduct(ProductDTO product){
        Product aux = new Product();

        dtoForEntity(product, aux);
        Date data = new Date();
        aux.setCreationDate(data);

        productRepo.saveProduct(aux);
        repository.save(aux);
        return 0;
    }

    public void serviceDel(Long id) throws ProductNotFoundException{
        verifyIfExists(id);
        repository.deleteById(id);
    }
    public void serviceDelCategory(String category) throws ProductNotFoundException{
        verifyIfCategoryExists(category);
        Optional<List<Product>> resultBank = repository.findByCategoryContaining(category);
        if (resultBank.isPresent()) {
            List<Product> users = resultBank.get();
            for (Product product : users) {
                repository.deleteById(product.getId());
            }
        }
    }
    public void serviceDelBrand(String brand){

        Optional<List<Product>> resultBank = repository.findByBrandContaining(brand);
        if (resultBank.isPresent()) {
            List<Product> users = resultBank.get();
            for (Product product : users) {
                repository.deleteById(product.getId());
            }
        }
    }
    public void serviceDelAll(){
        repository.deleteAll();
    }

    public void updateProduct(long id, ProductDTO product) throws ProductNotFoundException{
        Product aux = new Product();

        verifyIfExists(id);
        aux.setId(id);
        dtoForEntity(product, aux);
        repository.save(aux);
    }

    public void updatePromotionCategory(String category, float percent){
        Optional<List<Product>> resultBank = repository.findByCategoryContaining(category);
        changePercent(percent,resultBank);
    }

    public void updatePromotionBrand(String brand, float percent){
        Optional<List<Product>> resultBank = repository.findByBrandContaining(brand);
        changePercent(percent,resultBank);
    }

    public void updatePromotionAll(float percent){
        Optional<List<Product>> resultBank = repository.getByPromotionIsTrue();
        changePercent(percent, resultBank);
    }


    private void changePercent(float percent, Optional<List<Product>> resultBank) {
        if (resultBank.isPresent()) {
            List<Product> users = resultBank.get();
            for (Product product : users) {
                product.setPercent(percent);
                repository.save(product);
            }
        }
    }

    private boolean firstVerifyNumber(String str){
        if(str.substring(0,1).equals("0") || str.substring(0,1).equals("1") || str.substring(0,1).equals("2") || str.substring(0,1).equals("3") || str.substring(0,1).equals("4") || str.substring(0,1).equals("5") || str.substring(0,1).equals("6") || str.substring(0,1).equals("7") || str.substring(0,1).equals("8") || str.substring(0,1).equals("9")){
            return true;
        }
        return false;
    }
    private boolean firstVerifySpecial(String str){
        if(str.substring(0,1).equals("@") || str.substring(0,1).equals("#") || str.substring(0,1).equals("$") || str.substring(0,1).equals("%") || str.substring(0,1).equals("&") || str.substring(0,1).equals("*") || str.substring(0,1).equals("_")){
            return true;
        }
        return false;
    }
    private boolean verifySpecial(String str){
        if(str.contains(".") || str.contains("_") || str.contains("$") || str.contains("@") || str.contains("#") || str.contains("%") || str.contains("*") || str.contains("&")){
            return true;
        }
        return false;
    }
    private boolean verifyNumber(String str){
        if(str.contains("0") || str.contains("1") || str.contains("2") || str.contains("3") || str.contains("4") || str.contains("5") || str.contains("6") || str.contains("7") || str.contains("8") || str.contains("9")){
            return true;
        }
        return false;
    }

    private int productRules(ProductDTO product) {
        if(!(product.getName().trim().substring(0,1).equals(product.getName().trim().substring(0,1).toUpperCase())) || product.getName().equals("")){
            return 1;
        }
        if(!(product.getBrand().trim().substring(0,1).equals(product.getBrand().trim().substring(0,1).toUpperCase())) || product.getBrand().equals("")){
            return 2;
        }
        if(product.getQuantity() <= 0){
            return 3;
        }
        if(product.getPrice() <= 0.0){
            return 4;
        }
//        if(!(product.getCategory().trim().substring(0,1).equals(product.getCategory().trim().substring(0,1).toUpperCase())) || product.getCategory().equals("") || firstVerifySpecial(product.getCategory().trim()) || firstVerifyNumber(product.getCategory().trim()) || verifySpecial(product.getCategory().trim()) || verifyNumber(product.getCategory().trim())){
//            return 5;
//        }
        if(product.getPercent() < 0.0){
            return 6;
        }
        if(product.getDescription().trim().split(" ").length < 2 || firstVerifyNumber(product.getDescription().trim()) || firstVerifySpecial(product.getDescription().trim()) || !product.getDescription().trim().substring(0,1).equals(product.getDescription().trim().substring(0,1).toUpperCase())){
            return 7;
        }
        return 8;
    }

    private void dtoForEntity(ProductDTO product, Product aux) {
        aux.setId(product.getId());
        aux.setName(product.getName());
        aux.setBrand(product.getBrand());
        aux.setQuantity(product.getQuantity());
        aux.setPromotion(product.isPromotion());
        aux.setPrice(product.getPrice());
        aux.setDescription(product.getDescription());
        aux.setPercent(product.getPercent());
        aux.setCategory(product.getCategory());
    }

    private boolean verifyPercent(float percent){
        if(percent < 0.0)
            return true;
        return false;
    }

    private boolean verifyBrand(String brand){
        if(!(brand.trim().substring(0,1).equals(brand.trim().substring(0,1).toUpperCase())) || brand.equals(""))
            return true;
        return false;
    }

    private boolean verifyCategory(String category){
        if(!(category.trim().substring(0,1).equals(category.trim().substring(0,1).toUpperCase())) || category.equals("") || firstVerifySpecial(category.trim()) || firstVerifyNumber(category.trim()) || verifySpecial(category.trim()) || verifyNumber(category.trim())){
            return true;
        }
        return false;
    }

    private Product verifyIfExists(Long id) throws ProductNotFoundException{
        return repository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
    }

    private List<Product> verifyIfCategoryExists(String category) throws ProductNotFoundException{
        return repository.findByCategoryContaining(category).orElseThrow(()->new ProductNotFoundException("Category "+ category));
    }

    private void entityForDto(List<ProductDTO> listProducts, Optional<List<Product>> resultBank) {
        if (resultBank.isPresent()) {
            List<Product> users = resultBank.get();
            for (Product product : users) {
                ProductDTO dto = new ProductDTO(product.getId(), product.getName(), product.getBrand(), product.getPriceDto(), product.getQuantity(), product.getDescription(), product.isPromotion(), product.getPercent(), product.getCategory());
                listProducts.add(dto);
            }
        }
    }
}