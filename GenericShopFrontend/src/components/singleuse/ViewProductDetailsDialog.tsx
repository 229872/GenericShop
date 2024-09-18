import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Stack, Typography } from "@mui/material";
import axios from "axios";
import { CSSProperties, useEffect, useState } from "react";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";
import handleAxiosException from "../../services/apiService";
import GridCard from "../reusable/GridCard";
import { GridItemData } from "../../utils/types";
import { camelCaseToWords } from "../../services/textService";
import productNotFound from '/src/assets/no-product-picture.png'

type ProductData = {
  id: number
  version: string
  archival: boolean
  name: string
  price: number
  quantity: number
  imageUrl: string
  rates: 0 | 1 | 2 | 3 | 4 | 5[]
  categoryProperties: Object
}

type ViewProductDetailsDialogProps = {
  productId: number
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function ViewProductDetailsDialog({ open, onClose, setLoading, style, productId } : ViewProductDetailsDialogProps) {
  const { t } = useTranslation();
  const [ product, setProduct ] = useState<ProductData | undefined>(undefined)
  const [ productData, setProductData ] = useState<GridItemData[]>([])


  useEffect(() => {
    sendGetProductRequest(productId)
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

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
      <DialogTitle align='center' fontSize='35px'>{product?.name}</DialogTitle>
      <DialogContent>
        <Grid container spacing={2} maxHeight='500px'>
          <Grid item xs={12} md={7}>
            <GridCard noBorder data={productData} rowSpacing={2} />
          </Grid>
          <Grid item xs={12} md={5} maxHeight='500px'>
            <Box
              marginTop='30px'
              marginRight='10px'
              component="img"
              sx={{
                height: 300,
                width: '100%',
                maxHeight: { xs: 300, md: 300 },
                maxWidth: { xs: '100%', md: '100%' },
              }}
              alt={t('manage_products.view_product.label.product_image')}
              src={product?.imageUrl ?? productNotFound}
            />
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