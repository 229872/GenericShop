import { useEffect, useState } from "react"
import { BasicProduct } from "../utils/types"
import { getProductsFromLocalStorage } from "../services/cartService"
import { Box, Avatar, Typography, IconButton, Stack, Grid, Tooltip } from "@mui/material"
import CloseIcon from '@mui/icons-material/Close';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import { t } from "i18next";

type CartPageProps = {
  setLoading: (value: boolean) => void
  style?: React.CSSProperties
}

export default function CartPage({ setLoading, style }: CartPageProps) {
  const [cartProducts, setCartProducts] = useState<BasicProduct[]>([]);
  const [totalPrice, setTotalPrice] = useState<number>(0);

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
      // todo add toast with i18n that quantity must be at least 1
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
  };

  return <>
    <Stack spacing={4} direction='column' sx={{ ...style }}>
      {
        cartProducts.map(product => (<Box display='flex' alignItems='center' gap={2}>
          <Box
            marginTop='0px'
            marginRight='0px'
            component="img"
            sx={{
              height: 70,
              width: 50,
              maxHeight: { xs: 70, md: 50 },
              maxWidth: { xs: 70, md: 50 },
            }}
            alt={t('manage_products.view_product.label.product_image')}
            src={product.imageUrl}
          />

          <Grid container width='70%'>
            <Grid item xs={12}>
              <Typography variant='h6'>{product.name}</Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant='body1'>
                {t('manage_products.edit_product.label.quantity')}:
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant='body1'>
                {product.quantity}
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant='body1'>
                {t('manage_products.edit_product.label.price')}:
              </Typography>
            </Grid>
            <Grid item xs={6}>
              <Typography variant='body1'>
                {product.price}
              </Typography>
            </Grid>
          </Grid>

          <Stack direction='column' spacing={1}>
            <Tooltip title={'Remove product from cart'} children={
              <IconButton color="primary">
                <CloseIcon />
              </IconButton>
            } />

            <Tooltip title={'Increase quantity'} children={
              <IconButton color="primary">
                <AddIcon />
              </IconButton>
            } />

            <Tooltip title={'Decrease quantity'} children={
              <IconButton color="primary">
                <RemoveIcon />
              </IconButton>
            } />

          </Stack>
        </Box>))
      }
    </Stack>
  </>
}