import axios from "axios";
import { environment } from "../utils/constants";
import { ProductData } from "../utils/types";
import { getJwtToken, getLogin } from "./tokenService";


export type UserPreferences = {
  categoryPreferences: { [category: string]: number }
  productPreferences: { [productId: number]: number };
}

export type Product = ProductData;

export function getUserPreferences(login: string): UserPreferences {
  const preferences = localStorage.getItem(`${login}-${environment.preferences}`);

  if (preferences) {
    return JSON.parse(preferences);
  }

  return {
    categoryPreferences: {},
    productPreferences: {},
  };
}

export function saveUserPreferences(login: string, preferences: UserPreferences): void {
  localStorage.setItem(`${login}-${environment.preferences}`, JSON.stringify(preferences));
}

export function updatePreference(type: 'category' | 'product', data: string | number, weight: number = 1): void {
  try {
      const login: string = getLogin(getJwtToken()) ?? "";
      if (login === "") return;
      const preferences: UserPreferences = getUserPreferences(login);
    
      if (type === 'category' && typeof data === 'string') {
    
        // Increment category count by the provided weight
        preferences.categoryPreferences[data] = (preferences.categoryPreferences[data] || 0) + weight;
    
      } else if (type === 'product' && typeof data === 'number') {
    
        // Increment product count by the provided weight
        preferences.productPreferences[data] = (preferences.productPreferences[data] || 0) + weight;
      }
    
      saveUserPreferences(login, preferences);
  } catch (e) {
    console.info('Couldn\'t update preference') 
  }
}

export const getRecommendedProducts = async (recordNumber: number) => {
  const preferences: UserPreferences = getUserPreferences(getLogin(getJwtToken()) ?? "")
  return axios.post(`${environment.apiBaseUrl}/products/recommended?size=${recordNumber}`, preferences, {
    headers: {
      Authorization: `Bearer ${getJwtToken()}`
    }
  })
}