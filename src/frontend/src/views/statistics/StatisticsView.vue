<template>
  <h1>Estatísticas dos Alunos</h1>
  <div style="height: 400px">
    <Bar :data="chartData" :options="chartOptions" />
  </div>
  <h2>Distribuição das Notas</h2>
  <div style="height: 400px">
    <Pie :data="pieChartData" :options="pieChartOptions" />
  </div>
</template>

<script setup lang="ts">
import { Bar, Pie } from 'vue-chartjs'
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  ArcElement,
  CategoryScale,
  LinearScale
} from 'chart.js'
import { ref } from 'vue'
import type PeopleDto from '@/models/PeopleDto'
import RemoteService from '@/services/RemoteService'

// Registrar Chart.js components
ChartJS.register(Title, Tooltip, Legend, BarElement, ArcElement, CategoryScale, LinearScale)

// Contadores para workflowTese
const staffTese = ref(0)
const signedTese = ref(0)
const atributedTese = ref(0)
const approvedTese = ref(0)
const submitedTese = ref(0)

// Contadores para workflowDef
const staffDef = ref(0)
const signedDef = ref(0)
const atributedDef = ref(0)
const approvedDef = ref(0)
const submitedDef = ref(0)

const chartData = ref({
  labels: [
    'Submetido ao Fenix',
    'Documento Assinado',
    'Presidente do Júri Atribuído',
    'Aprovado pelo SC',
    'Proposta de Júri Submetida'
  ],
  datasets: [
    {
      label: 'workflowTese',
      backgroundColor: '#42A5F5',
      data: [0, 0, 0, 0, 0]
    },
    {
      label: 'workflowDef',
      backgroundColor: '#66BB6A',
      data: [0, 0, 0, 0, 0]
    }
  ]
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false
}

// Pie Chart (grades)
const pieChartData = ref({
  labels: ['0–9', '10–12', '13–15', '16–17', '18–20'],
  datasets: [
    {
      label: 'Distribuição de Notas',
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#81C784', '#9575CD'],
      data: [0, 0, 0, 0, 0]
    }
  ]
})

const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false
}

const people = ref<PeopleDto[]>([])

getStudents()

async function getStudents() {
  people.value = await RemoteService.getStudents()

  staffTese.value = signedTese.value = atributedTese.value = approvedTese.value = submitedTese.value = 0
  staffDef.value = signedDef.value = atributedDef.value = approvedDef.value = submitedDef.value = 0

  const gradeBuckets = [0, 0, 0, 0, 0, 0] 

  people.value.forEach(student => {
    // workflowTese
    switch (student.workflowTese) {
      case 'STAFF': staffTese.value++; break
      case 'SIGNED': signedTese.value++; break
      case 'ATRIBUTED': atributedTese.value++; break
      case 'APPROVED': approvedTese.value++; break
      case 'SUBMITED': submitedTese.value++; break
    }

    // workflowDef
    switch (student.workflowDef) {
      case 'STAFF': staffDef.value++; break
      case 'SIGNED': signedDef.value++; break
      case 'ATRIBUTED': atributedDef.value++; break
      case 'APPROVED': approvedDef.value++; break
      case 'SUBMITED': submitedDef.value++; break
    }

    // Grades (supondo que student.grade é numérico)
    const grade = student.grade
    if (grade != null) {
      if (grade <= 0) gradeBuckets[0]++
      else if (grade < 10) gradeBuckets[1]++
      else if (grade <= 12) gradeBuckets[2]++
      else if (grade <= 15) gradeBuckets[3]++
      else if (grade <= 17) gradeBuckets[4]++
      else gradeBuckets[5]++
    }
  })

  // Atualizar dados do gráfico de barras
  chartData.value = {
    labels: [
      'Submetido ao Fenix',
      'Documento Assinado',
      'Presidente do Júri Atribuído',
      'Aprovado pelo SC',
      'Proposta de Júri Submetida'
    ],
    datasets: [
      {
        label: 'workflowTese',
        backgroundColor: '#42A5F5',
        data: [
          staffTese.value,
          signedTese.value,
          atributedTese.value,
          approvedTese.value,
          submitedTese.value
        ]
      },
      {
        label: 'workflowDef',
        backgroundColor: '#66BB6A',
        data: [
          staffDef.value,
          signedDef.value,
          atributedDef.value,
          approvedDef.value,
          submitedDef.value
        ]
      }
    ]
  }

  // Atualizar dados do gráfico de pizza
  pieChartData.value = {
    labels: ['Sem Nota', '0–9', '10–12', '13–15', '16–17', '18–20'],
    datasets: [
      {
        label: 'Distribuição de Notas',
        backgroundColor: ['#FF0000','#FF6384', '#36A2EB', '#FFCE56', '#81C784', '#9575CD'],
        data: gradeBuckets
      }
    ]
  }
}
</script>
