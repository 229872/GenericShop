import { Card, Table, TableBody, TableCell, TableContainer, TableHead, TablePagination, TableRow, TableSortLabel, Typography } from "@mui/material"
import React, { FormEvent, ReactNode } from "react"
import { useTranslation } from "react-i18next"
import { Column } from "../../utils/types"
import { AxiosResponse } from "axios"

type TableWithPaginationProps<T> = {
  columns: Column<T>[]
  data: T[]
  getData: (pageNr: number, pageSize: number, sortBy: keyof T, direction: 'asc' | 'desc') => Promise<AxiosResponse> | Promise<void>
  totalElements: number
  sortBy: keyof T
  setSortBy: (column: keyof T) => void
  rowsPerPageOptions: number[]
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  setPageSize: (pageSize: number) => void
  direction: 'asc' | 'desc'
  setDirection: (direction: 'asc' | 'desc') => void
  tableStyle?: React.CSSProperties
  headerStyle?: React.CSSProperties
  contentStyle?: React.CSSProperties
}

export default function TableWithPagination<T>({ columns, data, getData, totalElements, sortBy, setSortBy, rowsPerPageOptions,
  currentPage, setCurrentPage, pageSize, setPageSize, direction, setDirection, tableStyle, headerStyle, contentStyle } : TableWithPaginationProps<T>) {

  const { t } = useTranslation();

  const changePage = (event: any, page: number): void => {
    setCurrentPage(page)
    getData(page, pageSize, sortBy, direction)
  }

  const changeRowsPerPage = (event: any): void => {
    const newPageSize = event.target.value
    setPageSize(newPageSize)
    getData(currentPage, newPageSize, sortBy, direction)
  }

  const handleSortChange = (event: FormEvent<HTMLSpanElement>, column: keyof T): void => {
    
    if (column === sortBy) {
      const newDirection = direction === 'asc' ? 'desc' : 'asc'
      changeDirection(newDirection)
    } else {
      changeSortBy(column)
    }
  }

  const changeDirection = (newDirection: 'asc' | 'desc') => {
    setDirection(newDirection)
    getData(currentPage, pageSize, sortBy, newDirection)
  }

  const changeSortBy = (newSortBy: keyof T) => {
    setSortBy(newSortBy)
    getData(currentPage, pageSize, newSortBy, direction)
  }

  const getPropertyAsNode = <T, K extends keyof T>(obj: T, key: K): ReactNode => {
    const value = obj[key];
    switch (typeof value) {
      case 'number':
      case 'string':
        return value;
      case 'boolean':
        return value ? t('table.yes') : t('table.no')
      case 'object':
        if (React.isValidElement(value)) {
          return value;
        } else if (Array.isArray(value)) {
          return value.join(', ');
        }
        return 'Not simple value'
      default:
        return 'Not simple value'
    }
  }

  if (data.length == 0) {
    return <Typography variant='h6'>{t('table.no_content')}</Typography>
  } else {
    return (
      <Card elevation={10} sx={{...tableStyle}}>
        <TableContainer>
          <Table stickyHeader>
            <TableHead>
              <TableRow>
                {
                  columns.map((column, key) => (
                    <TableCell key={key} sx={{ ...headerStyle }}>
                      <TableSortLabel
                        active={sortBy === column.dataProp}
                        direction={direction}
                        onClick={e => handleSortChange(e, column.dataProp)}
                      >
                        {column.name}
                      </TableSortLabel>
                    </TableCell>
                  ))
                }
              </TableRow>
            </TableHead>
            <TableBody>
              {
                data && data.map((obj, key) => (
                  <TableRow key={key}>
                    {
                      columns.map((col, key) => (
                        <TableCell key={key} sx={{...contentStyle}}>
                          {
                            getPropertyAsNode(obj, col.dataProp)
                          }
                        </TableCell>
                      ))
                    }
                  </TableRow>
                ))
              }
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          component='div'
          page={currentPage}
          onPageChange={changePage}
          count={totalElements}
          rowsPerPage={pageSize}
          rowsPerPageOptions={rowsPerPageOptions}
          onRowsPerPageChange={changeRowsPerPage}
        />
      </Card>
    )
  }
}