<template>
  <UtilBar />
  <NavBar :navbarItems="navbarItems" />
</template>

<script setup lang="ts">
import UtilBar from '@/components/UtilBar.vue'
import NavBar from '@/components/NavBar.vue'

const navbarItems = [
  { name: 'Home', path: '/'},
  { name: 'Alunos', path: '/students', icon: 'mdi-account-group' },
  { name: 'Pessoal', path: '/people', icon: 'mdi-account-group' },
  { name: 'Estatísticas', path: '/statistics', icon: 'mdi-chart-bar' },
]
</script>
