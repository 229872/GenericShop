import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Rating, Stack, Typography } from "@mui/material";
import axios from "axios";
import { CSSProperties, useEffect, useState } from "react";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";
import handleAxiosException from "../../services/apiService";
import GridCard from "../reusable/GridCard";
import { BasicProduct, GridItemData } from "../../utils/types";
import productNotFound from '/src/assets/no-product-picture.png'
import { getActiveRole, getJwtToken } from "../../services/tokenService";
import RateReviewIcon from '@mui/icons-material/RateReview';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ViewProductDetailsDialog from "../reusable/ViewProductDetailsDialog";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import { toast } from "sonner";

type OrderData = {
  id: number
  version: string
  totalPrice: number
  productQuantity: number
  creationDate: string
  products: BasicProduct[]
}

type GridItemDataWithProductData = {
  productId: number
  orderedProductId: number
  imageUrl: string
  gridItemData: GridItemData[]
}

type RateItem = {
  rateValue: number | null
  isSubmitted: boolean
}

type ViewOrderDetailsDialogProps = {
  orderId: number
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  setNumberOfProductsInCart: (value: number) => void
  style?: CSSProperties
}

export default function ViewOrderDetailsDialog({ open, onClose, setLoading, style, orderId, 
  setNumberOfProductsInCart } : ViewOrderDetailsDialogProps) {
  const { t } = useTranslation();
  const [ productListData, setProductListData ] = useState<GridItemDataWithProductData[]>([])
  const [ visibleViewProductDetailsDialog, setVisibleViewProductDetailsDialog ] = useState<number | undefined>(undefined)
  const [ order, setOrder ] = useState<OrderData | undefined>(undefined)
  const [ rates, setRates ] = useState<Map<number, RateItem>>(new Map<number, RateItem>())

  useEffect(() => {
    sendGetOrderRequest(orderId)
  }, [orderId])

  const getOrder = async (orderId: number) => {
    return axios.get(`${environment.apiBaseUrl}/orders/id/${orderId}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const sendGetOrderRequest = async (orderId: number) => {
    try {
      setLoading(true)
      const { data } = await getOrder(orderId)
      const order: OrderData = data;
      setOrder(order)
      
      const ratesData: Map<number, RateItem> = new Map();
      order.products.forEach(product => {
        if (product.orderedProductId) {
          const productRate = product.rate ?? 0;
          ratesData.set(product.orderedProductId, {
            rateValue: productRate,
            isSubmitted: productRate !== 0
          })
        }
      })

      setRates(ratesData)

      const productsData: GridItemDataWithProductData[] = order.products.map(product => (
        {
          productId: product.id,
          orderedProductId: product.orderedProductId?? 0,
          imageUrl: product.imageUrl,
          gridItemData: [
            { label: 'manage_products.view_product.label.name', content: product?.name ?? '-' },
            { label: 'manage_products.view_product.label.price', content: product?.price ?? '-' },
            { label: 'manage_products.view_product.label.quantity', content: product?.quantity ?? '-' }
          ]
        }
      ))
      setProductListData(productsData)

    } catch (e) {
      handleAxiosException(e)
      onClose()

    } finally {
      setLoading(false)
    }
  }

  const applyNewRate = async (orderedProductId: number, rate: RateItem | undefined): Promise<void> => {
    try {
      setLoading(true)
      if (rate === undefined) return
      const newRateValue = rate.rateValue;
      const payload = { 
        rateValue: newRateValue
      }

      if (!rate.isSubmitted) {
        await axios.post(`${environment.apiBaseUrl}/orders/orderedProducts/${orderedProductId}/rate`, payload, {
          headers: {
            Authorization: `Bearer ${getJwtToken()}`
          }
        })
        handleRatingChange(orderedProductId, payload.rateValue, true)
        toast.success(t('self_orders.button.rate.add_success'))

      } else {
        await axios.put(`${environment.apiBaseUrl}/orders/orderedProducts/${orderedProductId}/rate`, payload, {
          headers: {
            Authorization: `Bearer ${getJwtToken()}`
          }
        })
        handleRatingChange(orderedProductId, payload.rateValue, true)
        toast.success(t('self_orders.button.rate.update_success'))
      }

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const removeRole = async (orderedProductId: number): Promise<void> => {
    try {
      setLoading(true)
      await axios.delete(`${environment.apiBaseUrl}/orders/orderedProducts/${orderedProductId}/rate`, {
        headers: {
          Authorization: `Bearer ${getJwtToken()}`
        }
      })
      handleRatingChange(orderedProductId, 0, false)
      toast.success(t('self_orders.button.rate.delete_success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const handleRatingChange = (orderedProductId: number, newValue: number | null, isSubmitted: boolean) => {
    const updatedRates = new Map(rates);

    updatedRates.set(orderedProductId, {
      rateValue: newValue,
      isSubmitted: isSubmitted
    });

    setRates(updatedRates);
  };

  const getRateValue = (orderedProductId: number): number | null => {
    return rates.get(orderedProductId)?.rateValue ?? null
  }

  const isRateSubmitted = (orderedProductId: number): boolean => {
    return rates.get(orderedProductId)?.isSubmitted ?? false;
  }

  return (
    <>
      <Dialog open={open} onClose={onClose} sx={{ marginTop: '4vh' }} maxWidth={false} PaperProps={{sx: { width: '60vw' }}}>
        <DialogTitle align='center' fontSize='35px'>{t('self_orders.detail.order_products')}</DialogTitle>
        <DialogContent>
          <Grid container marginTop='15px'>
            <Grid container item xs={5}>
              <Grid item xs={8}>
                <Typography variant='h5'>{t('self_orders.column.quantity')}:</Typography>
              </Grid>

              <Grid item xs={4}>
                <Typography variant='h5'>{order?.productQuantity}</Typography>
              </Grid>
            </Grid>

            <Grid item xs={1} />

            <Grid container item xs={6}>
              <Grid item xs={6}>
                <Typography variant='h5'>{t('self_orders.column.creation_date')}:</Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant='h5'>{order?.creationDate}</Typography>
              </Grid>
            </Grid> 
          </Grid>

          {
            productListData.map((productData, key) => (
              <Grid key={key} container spacing={2} maxHeight='500px' marginTop='20px'>
                <Grid item xs={6}>
                  <GridCard data={productData.gridItemData} rowSpacing={2} />
                </Grid>

                <Grid item xs={3}>
                  <Stack spacing={1} sx={{ height: '100%' }}>
                    <Button endIcon={<VisibilityIcon />} color='inherit' onClick={() => setVisibleViewProductDetailsDialog(productData.productId)}>
                      {t('self_orders.tooltip.show_more')}
                    </Button>

                    <Stack direction='row' justifyContent='center'>
                      <Rating
                        name="rate-product"
                        value={rates.get(productData.orderedProductId)?.rateValue}
                        onChange={(event, newValue) => {
                          handleRatingChange(productData.orderedProductId, newValue, rates.get(productData.orderedProductId)?.isSubmitted ?? false);
                        }}
                        precision={1}
                        max={5}
                      />
                    </Stack>
                    
                    <Button 
                      disabled={getRateValue(productData.orderedProductId) === 0}
                      endIcon={<RateReviewIcon />}
                      color='inherit'
                      onClick={() => applyNewRate(productData.orderedProductId, rates.get(productData.orderedProductId))}
                    >
                      {t('self_orders.tooltip.rate_product')}
                    </Button>
                    {getRateValue(productData.orderedProductId) !== null && isRateSubmitted(productData.orderedProductId) && (
                      <Button endIcon={<DeleteOutlineIcon />} color='inherit' onClick={() => removeRole(productData.orderedProductId)}>
                        {t('self_orders.tooltip.delete_rate')}
                      </Button>
                    )}

                  </Stack>
                </Grid>

                  
                <Grid item xs={3} maxHeight='500px'>
                  <Box
                    component="img"
                    sx={{
                      height: 150,
                      maxHeight: { xs: 150, md: 150 },
                      maxWidth: { xs: '100%', md: '100%' },
                    }}
                    alt={t('manage_products.view_product.label.product_image')}
                    src={productData?.imageUrl ?? productNotFound}
                  />
                </Grid>
              </Grid>
            ))
          }
        </DialogContent>
        <DialogActions>
          <Stack direction='row' spacing={3}>
            <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
          </Stack>
        </DialogActions>
      </Dialog>

      {
        visibleViewProductDetailsDialog &&
          <ViewProductDetailsDialog
            productId={visibleViewProductDetailsDialog}
            open={Boolean(setVisibleViewProductDetailsDialog)}
            onClose={() => setVisibleViewProductDetailsDialog(undefined)}
            activeRole={getActiveRole(getJwtToken())}
            setVisibleViewProductDetailsDialog={setVisibleViewProductDetailsDialog}
            setNumberOfProductsInCart={setNumberOfProductsInCart}
            setLoading={setLoading}
          />
      }
    </>
  )
}