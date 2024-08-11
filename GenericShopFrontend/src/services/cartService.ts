import axios from "axios";
import { BasicProduct } from "../utils/types";
import { getJwtToken, getLogin } from "./tokenService";
import { environment } from "../utils/constants";

const saveCart = (localStorageKey: string, products: BasicProduct[]): void => {
  localStorage.setItem(localStorageKey, JSON.stringify(products))
}

export const addToCart = (productToAdd: BasicProduct): void => {
  productToAdd.quantity = 1;
  const login = getLogin(getJwtToken());
  const localStorageKey = `${login}-cart`; 
  const cartProducts: BasicProduct[] = getCart(localStorageKey);
  let isNewProduct = true;

  const modifiedProducts: BasicProduct[] = cartProducts.map(product => {
    if (product.id === productToAdd.id) {
      ++product.quantity
      isNewProduct = false;
    }
    return product;
  })

  if (isNewProduct) {
    modifiedProducts.push(productToAdd)
  }

  saveCart(localStorageKey, modifiedProducts)
}

export const clearCart = (localStorageKey: string): void => {
  localStorage.removeItem(localStorageKey)
  saveCart(localStorageKey, []);
}

export const getTotalAmountOfProducts = (): number => {
  const products: BasicProduct[] = getProductsFromLocalStorage()
  return products.reduce((prev, cur) => prev + cur.quantity, 0)
}

const getCart = (localStorageKey: string): BasicProduct[] => {
  const products: string | null = localStorage.getItem(localStorageKey)
  return products ? JSON.parse(products) : [];
}

export const getProductsFromLocalStorage = (): BasicProduct[] => {
  const login = getLogin(getJwtToken());
  const localStorageKey = `${login}-cart`;

  const products: BasicProduct[] = getCart(localStorageKey);
  return products;

  // Powinienem odczytać jeszcze raz każdy produkt z koszyka, żeby porównać dane z api z danymi z koszyka,
  // które mogą być już nieaktualne, jeżeli cena lub ilość jest zmieniona powinno być to uaktualnione
}

export const removeProduct = (products: BasicProduct[], orderedProduct: BasicProduct, localStorageKey: string): void => {
  products.forEach((product, index) => {
    if (orderedProduct.id === product.id) {
      products.splice(index, 1);
    }
  })
  saveCart(localStorageKey, products);
}

export const isAnyProductArchival = (products: BasicProduct[]): boolean => {
  return products.some(product => product.archival)
}

const getSingleProduct = async (id: number) => {
  return axios.get(`${environment.apiBaseUrl}/products/id/${id}/short`, {
    headers: {
      Authorization: `Bearer ${getJwtToken()}`
    }
  })
}

const retrieveCartProduct = async (id: number) : Promise<BasicProduct | undefined> => {
  try {

    const { data } = await getSingleProduct(id);
    return data;

  } catch (e) {
    return undefined;
  }
}