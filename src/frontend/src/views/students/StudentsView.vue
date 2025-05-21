<template>
  <v-row align="center">
    <v-col>
      <h2 class="text-left ml-1">Listagem de Alunos</h2>
    </v-col>
  </v-row>

  <!-- Filters and Search -->
  <v-row dense class="mb-4">
    <v-col cols="auto">
      <v-text-field
        v-model="search"
        label="Search"
        prepend-inner-icon="mdi-magnify"
        variant="outlined"
        hide-details
        single-line
        density="compact"
        style="width: 200px"
      />
    </v-col>

    <v-col cols="auto">
      <v-select
        v-model="selectedWorkflowTese"
        :items="Object.keys(workflowTeseLabels)"
        :item-title="(val) => workflowTeseLabels[val]"
        label="Workflow Tese"
        clearable
        variant="outlined"
        hide-details
        density="compact"
        style="width: 180px"
      />
    </v-col>

    <v-col cols="auto">
      <v-select
        v-model="selectedWorkflowDef"
        :items="Object.keys(workflowDefLabels)"
        :item-title="(val) => workflowDefLabels[val]"
        label="Workflow Defesa"
        clearable
        variant="outlined"
        hide-details
        density="compact"
        style="width: 180px"
      />
    </v-col>
  </v-row>

  <!-- Data Table -->
  <v-data-table
    :headers="headers"
    :items="filteredPeople"
    :search="search"
    :loading="loading"
    item-key="id"
    class="text-left"
    no-data-text="Sem alunos a apresentar."
  >
    <!-- Chips for workflowTese -->
    <template v-slot:[`item.workflowTese`]="{ item }">
      <v-chip v-if="item.workflowTese === 'STAFF'" color="blue" text-color="white">
        Submetido ao Fenix
      </v-chip>
      <v-chip v-else-if="item.workflowTese === 'SIGNED'" color="green" text-color="white">
        Documento Assinado
      </v-chip>
      <v-chip v-else-if="item.workflowTese === 'ATRIBUTED'" color="yellow" text-color="white">
        Presidente do Júri Atribuído
      </v-chip>
      <v-chip v-else-if="item.workflowTese === 'APPROVED'" color="orange" text-color="white">
        Aprovado pelo SC 
      </v-chip>
      <v-chip v-else-if="item.workflowTese === 'SUBMITED'" color="red" text-color="white">
        Proposta de Júri Submetida 
      </v-chip>
      <v-chip v-else text-color="black">
        Inicio
      </v-chip>
    </template>

    <!-- Chips for workflowDef -->
    <template v-slot:[`item.workflowDef`]="{ item }">
      <v-chip v-if="item.workflowDef === 'SUBMITED'" color="blue" text-color="white">
        Submetido ao Fenix
      </v-chip>
      <v-chip v-else-if="item.workflowDef === 'REVISION'" color="green" text-color="white">
        Em Revisão
      </v-chip>
      <v-chip v-else-if="item.workflowDef === 'SCHEDULED'" color="yellow" text-color="white">
        Defesa Agendada
      </v-chip>
      <v-chip v-else text-color="black">
        Inicio
      </v-chip>
    </template>

    <!-- Actions -->
    <template v-slot:[`item.actions`]="{ item }">
      <div class="d-flex">
        <StudentPage @person-edited="getStudents" :person="item" />
      </div>
    </template>
  </v-data-table>
</template>


<script setup lang="ts">
import type PeopleDto from '@/models/PeopleDto'
import RemoteService from '@/services/RemoteService'
import StudentPage from './StudentPage.vue'
import { reactive, ref, computed} from 'vue'

let search = ref('')
let loading = ref(true)
const headers = [
  { 
    title: 'ID',
    key: 'id',
    value: 'id',
    sortable: true,
    filterable: false
   },
  {
    title: 'Nome',
    key: 'name',
    value: 'name',
    sortable: true,
    filterable: true
  },
  {
    title: 'IST ID',
    key: 'istId',
    value: 'istId',
    sortable: true,
    filterable: true
  },
  {
    title: 'Email',
    key: 'email',
    value: 'email',
    sortable: true,
    filterable: true
  },
  {
    title: 'Workflow Tese',
    key: 'workflowTese',
    value: 'workflowTese',
    sortable: true,
    filterable: true
  },
  {
    title: 'Workflow Defesa',
    key: 'workflowDef',
    value: 'workflowDef',
    sortable: true,
    filterable: true
  },
  {
    title: 'Ações',
    key: 'actions',
    value: 'actions',
    sortable: false,
    filterable: false
  }

]

const people: PeopleDto[] = reactive([])

let selectedWorkflowTese = ref<string | null>(null)
let selectedWorkflowDef = ref<string | null>(null)
const currentDate = new Date()

const workflowTeseLabels: Record<string, string> = {
  STAFF: 'Submetido ao Fenix',
  SIGNED: 'Documento Assinado',
  ATRIBUTED: 'Presidente do Júri Atribuído',
  APPROVED: 'Aprovado pelo SC',
  SUBMITED: 'Proposta de Júri Submetida',
}

const workflowDefLabels: Record<string, string> = {
  SUBMITED: 'Submetido ao Fenix',
  REVISION: 'Em Revisão',
  SCHEDULED: 'Defesa Agendada',
}

// Get the key for a type mapping (for reverse lookup)
function getItemKey(rec: Record<string, string>, value: string): string {
  for (const [key, v] of Object.entries(rec)) {
    if (v === value) {
      return key
    }
  }
}

getStudents()
async function getStudents() {

  people.splice(0, people.length)
  people.push(...(await RemoteService.getStudents()))
  loading.value = false
  
  people.forEach(student => {
    if (!student.defenseDate) return

    const defenseDate = new Date(student.defenseDate)

    if (defenseDate < currentDate) {
      student.workflowDef = 'REVISION'
  }})
  console.log(people)
}



const filteredPeople = computed(() => {
  const s = search.value.trim().toLowerCase()

  return people.filter(item => {
    // Apply search string over name, email, istId (fuzzy-ish logic)
    const matchesSearch = !s || (
      item.name?.toLowerCase().includes(s) ||
      item.email?.toLowerCase().includes(s) ||
      item.istId?.toLowerCase().includes(s)
    )

    // Apply workflow filters
    const matchesTese = !selectedWorkflowTese.value || item.workflowTese === getItemKey(workflowTeseLabels, selectedWorkflowTese.value)
    const matchesDef = !selectedWorkflowDef.value || item.workflowDef === getItemKey(workflowDefLabels, selectedWorkflowDef.value)

    return matchesSearch && matchesTese && matchesDef
  })
})

</script>