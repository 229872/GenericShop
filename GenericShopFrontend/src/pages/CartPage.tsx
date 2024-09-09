import { useEffect, useState } from "react"
import { BasicProduct } from "../utils/types"
import { getProductsFromLocalStorage, removeProductFromCart, addToCart, getTotalAmountOfProducts, clearCart } from "../services/cartService"
import { Box, Avatar, Typography, IconButton, Stack, Grid, Tooltip, Button, Card, CardContent } from "@mui/material"
import CloseIcon from '@mui/icons-material/Close';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import RemoveShoppingCartIcon from '@mui/icons-material/RemoveShoppingCart';
import { t } from "i18next";

type CartPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
  setNumberOfProductsInCart: (value: number) => void
}

export default function CartPage({ setLoading, style, setNumberOfProductsInCart }: CartPageProps) {
  const [cartProducts, setCartProducts] = useState<BasicProduct[]>([]);
  const [totalPrice, setTotalPrice] = useState<number>(0);
  const iconButtonPxNumber = 20;
  const changeQuantityButtonStyle = {
    padding: 0,
    minWidth: `${iconButtonPxNumber}px`,
    maxWidth: `${iconButtonPxNumber}20px`,
    minHeight: `${iconButtonPxNumber}px`,
    maxHeight: `${iconButtonPxNumber}px`,
  }
  const gridItemStyle = {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'left',
    height: '40px',
    gap: 1,
  }

  useEffect(() => {
    setLoading(true)
    const products: BasicProduct[] = getProductsFromLocalStorage();
    setCartProducts(products)
    const price: number = products.reduce((prev, product) => prev + (product.price * product.quantity), 0)
    setTotalPrice(price)
    setLoading(false)
  }, [])

  const updateTotalPrice = (products: BasicProduct[]) => {
    const total = products.reduce(
      (sum, orderedProduct) =>
        sum + orderedProduct.price * orderedProduct.quantity,
      0
    );
    setTotalPrice(total);
  };

  const decreaseAmount = (orderedProduct: BasicProduct) => {
    const updatedProduct: BasicProduct = { ...orderedProduct, quantity: orderedProduct.quantity - 1 };
    if (updatedProduct.quantity < 1) {
      updatedProduct.quantity = 1;
    }
    updateProductInCart(updatedProduct);
  };

  const increaseAmount = (orderedProduct: BasicProduct) => {
    const updatedProduct: BasicProduct = { ...orderedProduct, quantity: orderedProduct.quantity + 1 };
    // todo add check for max quantity of product
    updateProductInCart(updatedProduct);
  };

  const updateProductInCart = (updatedProduct: BasicProduct) => {
    const updatedProducts = cartProducts.map((product) =>
      product.id === updatedProduct.id ? updatedProduct : product
    );
    setCartProducts(updatedProducts)
    updateTotalPrice(updatedProducts);
    addToCart(updatedProduct)
    setNumberOfProductsInCart(getTotalAmountOfProducts())
  };

  const removeProduct = (product: BasicProduct): void => {
    const newProductList = cartProducts.filter(p => p.id !== product.id)
    setCartProducts(newProductList)
    removeProductFromCart(product)
    setNumberOfProductsInCart(getTotalAmountOfProducts())
  }

  const emptyCart = (): void => {
    // alert about submit
    setCartProducts([])
    clearCart()
    setNumberOfProductsInCart(getTotalAmountOfProducts())
  }

  const getTotalPrice = (): number => {
    return cartProducts.reduce((prev, cur) => prev + (cur.quantity * cur.price), 0)
  }

  const placeNewOrder = async (): Promise<void> => {
    console.log(cartProducts)
  } 

  return <>
    <Stack spacing={8} direction='column' sx={{ ...style }}>

      <Grid container>
        <Grid item xs={4} sx={{height: '80px'}}>
          <Stack direction='row' spacing={16} width='100%'>
            <Typography variant='h3'>{t('manage_products.view_product.cart')}</Typography>

            <Tooltip title={t('manage_products.view_product.clear_cart')} placement='right' children={
              <Button variant='outlined' color='inherit' onClick={() => emptyCart()}>
                <RemoveShoppingCartIcon fontSize='small' /><Typography variant='body1'> {t('manage_products.view_product.clear_cart')}</Typography>
              </Button>
            } />
          </Stack>
        </Grid>

        <Grid item xs={4} sx={{height: '80px'}} />
        
        <Grid item container xs={4} sx={{height: '80px'}}>
          <Card elevation={5} sx={{width: '100%'}}>
            <CardContent>
              <Stack spacing={5}>
                <Typography variant='h4'>{t('manage_products.view_product.summary')}</Typography>

                  <Stack direction='row' spacing={4}>
                    <Typography variant='h6'><b>{t('manage_products.edit_product.label.totalPrice')}:</b></Typography>
                    <Typography variant='h6'><b>{getTotalPrice()}</b></Typography>
                  </Stack>


                <Button sx={{width: '60%'}} variant='contained' color='primary' onClick={() => placeNewOrder()}>
                  {t('manage_products.view_product.placeOrder')}
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>



      {
        cartProducts.map((product) => (
          <Grid key={product.id} container width='50%' height='200px'>
            <Card elevation={2} sx={{ width: '100%', height: '100%', padding: '20px', paddingTop: '0px', display: 'flex' }}>
              <CardContent sx={{width: '100%', height: '100%', display: 'flex'}}>
                <Grid
                  item
                  xs={2}
                  sx={{
                    display: 'flex',
                    alignItems: 'stretch',
                    height: '100%',
                  }}
                >
                  <img
                    src={product.imageUrl}
                    alt={t('manage_products.view_product.label.product_image')}
                    style={{ height: '100%', width: '100%', objectFit: 'contain' }}
                  />
                </Grid>

                <Grid item xs={1} />

                <Grid item container xs={8}>
                  <Grid item xs={12} sx={{ height: '35%' }}>
                    <Typography variant="h6">{product.name}</Typography>
                  </Grid>

                  <Grid item container xs={12} sx={{ height: '65%' }}>
                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {t('manage_products.edit_product.label.quantity')}:
                      </Typography>
                    </Grid>

                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {product.quantity}
                      </Typography>

                      <Stack direction='column' spacing={0} >
                        <IconButton
                          color="primary"
                          size="small"
                          onClick={() => increaseAmount(product)}
                          sx={changeQuantityButtonStyle}
                        >
                          <ExpandLessIcon fontSize="small" />
                        </IconButton>

                        <IconButton
                          disabled={product.quantity === 1}
                          color="primary"
                          size="small"
                          onClick={() => decreaseAmount(product)}
                          sx={changeQuantityButtonStyle}
                        >
                          <ExpandMoreIcon fontSize="small" />
                        </IconButton>
                      </Stack>
                    </Grid>

                    <Grid item xs={6} />

                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {t('manage_products.edit_product.label.price')}:
                      </Typography>
                    </Grid>

                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {product.price}
                      </Typography>
                    </Grid>

                    <Grid item xs={6} />

                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {t('manage_products.edit_product.label.totalPrice')}:
                      </Typography>
                    </Grid>

                    <Grid item xs={3} sx={gridItemStyle}>
                      <Typography variant='body1'>
                        {product.price * product.quantity}
                      </Typography>
                    </Grid>

                    <Grid item xs={6} />

                  </Grid>
                </Grid>

                <Grid item
                  xs={1}
                  sx={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    height: '100%',
                  }}>
                  <Tooltip title={'Remove product from cart'}>
                    <IconButton onClick={() => removeProduct(product)} size="large" color="primary">
                      <CloseIcon />
                    </IconButton>
                  </Tooltip>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        ))
      }
    </Stack>
  </>
}