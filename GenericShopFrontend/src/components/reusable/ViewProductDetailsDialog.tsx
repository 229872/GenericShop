import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, IconButton, Stack, Tooltip, Typography } from "@mui/material";
import axios from "axios";
import { CSSProperties, useEffect, useState } from "react";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";
import handleAxiosException from "../../services/apiService";
import GridCard from "./GridCard";
import { GridItemData, ProductData, Role } from "../../utils/types";
import { camelCaseToWords } from "../../services/textService";
import productNotFound from '/src/assets/no-product-picture.png'
import { getRecommendedProducts, updatePreference } from "../../services/preferencesService";
import { isUserSignIn } from "../../services/sessionService";
import { toast } from "sonner";
import AddIcon from '@mui/icons-material/Add';
import VisibilityIcon from '@mui/icons-material/Visibility';
import { addToCart, getTotalAmountOfProducts } from "../../services/cartService";



type ViewProductDetailsDialogProps = {
  productId: number
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  setNumberOfProductsInCart: (value: number) => void
  setVisibleViewProductDetailsDialog: (value: number) => void
  activeRole: Role
  style?: CSSProperties
}

export default function ViewProductDetailsDialog({ open, onClose, setLoading, style, productId, activeRole,
   setNumberOfProductsInCart, setVisibleViewProductDetailsDialog } : ViewProductDetailsDialogProps) {
  const { t } = useTranslation();
  const [ product, setProduct ] = useState<ProductData | undefined>(undefined)
  const [ productData, setProductData ] = useState<GridItemData[]>([])
  const [ recommendedProducts, setRecommmendedProducts] = useState<ProductData[]>([])


  useEffect(() => {
    sendGetProductRequest(productId)
    sendRecommendationsRequest()
  }, [productId])

  const getProduct = async (productId: number) => {
    return axios.get(`${environment.apiBaseUrl}/products/id/${productId}`)
  }

  const sendGetProductRequest = async (productId: number) => {
    try {
      setLoading(true)
      const { data } = await getProduct(productId)
      const product: ProductData = data;
      const productData: GridItemData[] = [
        { label: 'manage_products.view_product.label.id', content: product?.id ?? '-' },
        { label: 'manage_products.view_product.label.name', content: product?.name ?? '-' },
        { label: 'manage_products.view_product.label.price', content: product?.price ?? '-' },
        { label: 'manage_products.view_product.label.quantity', content: product?.quantity ?? '-' },
        { label: 'manage_products.view_product.label.archival', content: t(product?.archival ? 
          'manage_products.view_product.label.archival.yes' : 'manage_products.view_product.label.archival.no'
        ) }
      ];

      setProduct(product)
      updatePreference("product", product.id)

      const dynamicProductData: GridItemData[] = Object.entries(product.categoryProperties).map(([key, value]) => ({
        label: camelCaseToWords(key),
        content: value ?? '-'
      }));

      setProductData([...productData, ...dynamicProductData])

    } catch (e) {
      handleAxiosException(e)
      onClose()

    } finally {
      setLoading(false)
    }
  }

  const sendRecommendationsRequest = async () => {
    try {
      setLoading(true)
      const { data } = await getRecommendedProducts()
      setRecommmendedProducts(data)

    } catch (e) {

    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style }} maxWidth='lg' PaperProps={{
      sx: {
        width: '80%',
        maxWidth: 'none',
        height: '80%',
        maxHeight: 'none',
      }
    }}
>
      <DialogTitle align='center' fontSize='35px'>{product?.name}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} maxHeight='500px'>
          <Grid item xs={4}>
            <GridCard noBorder labelSize={6} contentSize={6} data={productData} rowSpacing={2} />
          </Grid>
          <Grid item xs={4} maxHeight='500px'>
            <Box
              marginTop='30px'
              marginRight='10px'
              component="img"
              sx={{
                height: 350,
                maxHeight: { xs: 350, md: 350 }
              }}
              alt={t('manage_products.view_product.label.product_image')}
              src={product?.imageUrl ?? productNotFound}
            />
          </Grid>

          <Grid item xs={4}>
            {isUserSignIn() && (
              <>
                <Typography variant='h4'>{t('recommendation')}:</Typography>
                <Stack spacing={1} marginTop='20px'>
                  {
                    recommendedProducts.length > 0 && (
                      recommendedProducts.filter(p => p.id !== product?.id).slice(0, 5).map((p, key) => (
                        <Grid key={key} container border='1px solid gray' height='80px'>
                          <Grid item xs={3} height='100%'>
                            <Box
                              component="img"
                              sx={{
                                height: '100%',
                              }}
                              alt={t('manage_products.view_product.label.product_image')}
                              src={p?.imageUrl ?? productNotFound}
                            />
                          </Grid>

                          <Grid item xs={8}>
                            <Typography
                              sx={{
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                              }}
                              variant='h6'
                            >
                              {p.name}
                            </Typography>
                            <Typography variant='caption'>{p.price}</Typography>
                          </Grid>

                          <Grid item xs={1}>
                            <Tooltip title={t('manage_prodcuts.view_product.show_details')} placement='right' children={
                              <IconButton onClick={() => {
                                setVisibleViewProductDetailsDialog(p.id)
                              }}>
                                <VisibilityIcon />
                              </IconButton>
                            } />

                            {!p.archival && p.quantity > 0 && isUserSignIn() && activeRole === Role.CLIENT ? (
                              <Grid item xs={3}>
                                <Tooltip title={t('manage_products.view_product.add_to_cart')} placement='right' children={
                                  <IconButton onClick={() => {
                                    addToCart({...p, averageRating: 0.0})
                                    setNumberOfProductsInCart(getTotalAmountOfProducts())
                                    toast.success(t('manage_products.view_product.add_to_cart.success'))
                                  }}>
                                    <AddIcon />
                                  </IconButton>
                                } />
                              </Grid>
                            ) : <Grid item xs={3} />}
                          </Grid>
                        </Grid>
                      ))
                  )}
                </Stack>
              </>
              )
            }
          </Grid> 
        </Grid>
      </DialogContent>
      <DialogActions>
        <Stack direction='row' spacing={3}>
          <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
        </Stack>
      </DialogActions>
    </Dialog>
  )
}