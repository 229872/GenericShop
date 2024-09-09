import axios from "axios";
import { BasicProduct } from "../utils/types";
import { getJwtToken, getLogin } from "./tokenService";
import { environment } from "../utils/constants";

export const addToCart = (productToAdd: BasicProduct): void => {
  productToAdd = {...productToAdd}
  const productToAddQuantity = productToAdd.quantity;
  productToAdd.quantity = 1;
  const localStorageKey = getLocalStorageKey();
  const cartProducts: BasicProduct[] = getCartFromLocalStorage(localStorageKey);
  let isNewProduct = true;

  const modifiedProducts: BasicProduct[] = cartProducts.map(product => {
    if (product.id === productToAdd.id) {
      if (productToAddQuantity > product.quantity) {
        ++product.quantity
      }

      if (productToAddQuantity < product.quantity) {
        --product.quantity
      }
      isNewProduct = false;
    }
    return product;
  })

  if (isNewProduct) {
    modifiedProducts.push(productToAdd)
  }

  saveCartInLocalStorage(localStorageKey, modifiedProducts)
}

export const clearCart = (): void => {
  const localStorageKey = getLocalStorageKey();
  localStorage.removeItem(localStorageKey)
  saveCartInLocalStorage(localStorageKey, []);
}

export const getTotalAmountOfProducts = (): number => {
  const products: BasicProduct[] = getProductsFromLocalStorage()
  return products.reduce((prev, cur) => prev + cur.quantity, 0)
}

export const getProductsFromLocalStorage = (): BasicProduct[] => {
  const localStorageKey = getLocalStorageKey();

  const products: BasicProduct[] = getCartFromLocalStorage(localStorageKey);
  return products;

  // Powinienem odczytać jeszcze raz każdy produkt z koszyka, żeby porównać dane z api z danymi z koszyka,
  // które mogą być już nieaktualne, jeżeli cena lub ilość jest zmieniona powinno być to uaktualnione
}

export const removeProductFromCart = (product: BasicProduct) => {
  const localStorageKey = getLocalStorageKey()
  const products: BasicProduct[] = getCartFromLocalStorage(localStorageKey)
  removeProductFromLocalStorage(products, product, localStorageKey);
} 

export const isAnyProductArchival = (products: BasicProduct[]): boolean => {
  return products.some(product => product.archival)
}

const removeProductFromLocalStorage = (products: BasicProduct[], orderedProduct: BasicProduct, localStorageKey: string): void => {
  products.forEach((product, index) => {
    if (orderedProduct.id === product.id) {
      products.splice(index, 1);
    }
  })
  saveCartInLocalStorage(localStorageKey, products);
}

const saveCartInLocalStorage = (localStorageKey: string, products: BasicProduct[]): void => {
  localStorage.setItem(localStorageKey, JSON.stringify(products))
}

const getCartFromLocalStorage = (localStorageKey: string): BasicProduct[] => {
  const products: string | null = localStorage.getItem(localStorageKey)
  return products ? JSON.parse(products) : [];
}

const getLocalStorageKey = (): string => {
  const login = getLogin(getJwtToken());
  return `${login}-cart`;
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